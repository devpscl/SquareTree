package net.apicode.squaretree.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.apicode.squaretree.network.handler.PacketReceiver;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketType;
import net.apicode.squaretree.network.packet.response.RegisterResponse.Result;
import net.apicode.squaretree.network.packet.response.ResponseState;
import net.apicode.squaretree.network.protocol.PacketNetworkPing;
import net.apicode.squaretree.network.protocol.PacketNetworkRegister;
import net.apicode.squaretree.network.util.AsyncTask;
import net.apicode.squaretree.network.util.PacketUtil;

public class NettyServerChannelHandler extends SimpleChannelInboundHandler<Packet<?>> {

  private final BridgeServer bridgeServer;

  public NettyServerChannelHandler(BridgeServer bridgeServer) {
    this.bridgeServer = bridgeServer;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    NetworkNode node = new NetworkNode(bridgeServer, ctx.channel(), null);
    bridgeServer.getNodeMap().addNode(node);
    bridgeServer.foreachHandler(networkHandler -> {
      networkHandler.nodePreConnect(node);
    });
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    NetworkNode node = bridgeServer.getNode(ctx.channel());
    if(node != null) {
      bridgeServer.foreachHandler(networkHandler -> {
        networkHandler.nodeClose(node);
      });
      bridgeServer.getNodeMap().removeNode(node);
    }
  }

  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet) throws Exception {

    NetworkNode node = bridgeServer.getNode(channelHandlerContext.channel());
    if(node != null) {
      if(!node.isRegistered()) {
        if(packet instanceof PacketNetworkRegister) {
          PacketNetworkRegister packetNetworkRegister = (PacketNetworkRegister) packet;
          String privateKey = packetNetworkRegister.getPrivateKey();
          if(!bridgeServer.getSecurityInfo().getPrivateKey().equals(privateKey)) {
            PacketUtil.createCallback(node, packetNetworkRegister, response -> {
              response.setState(ResponseState.ERROR);
              response.setValue(Result.INVALID_PRIVATE_KEY);
            });
          } else if(bridgeServer.getNode(packet.getSenderNode()) != null) {
            PacketUtil.createCallback(node, packetNetworkRegister, response -> {
              response.setState(ResponseState.ERROR);
              response.setValue(Result.NODE_ALREADY_LOADED);
            });
          } else {
            node.register(packet.getSenderNode());
            bridgeServer.getNodeMap().registerNode(node);
            PacketUtil.createCallback(node, packetNetworkRegister, response -> {
              response.setState(ResponseState.SUCCESSFUL);
              response.setValue(Result.SUCCESSFUL);
            });
            AsyncTask.create(() -> bridgeServer.foreachHandler(networkHandler -> networkHandler.nodeConnect(node)));
          }
        }
        return;
      }
      Class<? extends Packet<?>> packetClass = (Class<? extends Packet<?>>) packet.getClass();

      if(!packet.getTargetNode().equals(bridgeServer.getNetworkNode().getId())) {
        NetworkNode targetNode = bridgeServer.getNode(packet.getTargetNode());
        if(targetNode == null) {
          PacketUtil.createCallback(node, packet, response -> {
            response.setState(ResponseState.TOTAL_ERROR);
          });
        } else {
          bridgeServer.sendPacket(packet, targetNode.getChannel());
        }
      } else {
        if(packet.getContainerType() == PacketType.REQUEST) {
          AsyncTask.create(() -> {
            if(acceptPacket(packet, node)) {
              for (PacketReceiver<?> packetListener : bridgeServer.getPacketListeners(packetClass)) {
                try {
                  packetListener.input(packet, node);
                } catch (Throwable t) {
                  exceptionCaught(channelHandlerContext, t);
                }
              }
            }
            try {
              PacketUtil.createCallback(node, packet);
            } catch (NetworkException e) {
              throw new RuntimeException(e);
            }
          });
        } else if(packet.getContainerType() == PacketType.RESPONSE) {
          bridgeServer.getWaitingQueue().forwardPacket(packet);
        }
      }
    }
  }

  private boolean acceptPacket(Packet<?> packet, NetworkNode networkNode) {
    if(packet instanceof PacketNetworkPing) {
      PacketNetworkPing pingPacket = (PacketNetworkPing) packet;
      pingPacket.getResponse().setValue(pingPacket.getTimeAtSend());
      return false;
    }
    return true;
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    NetworkNode node = bridgeServer.getNode(ctx.channel());
    bridgeServer.foreachHandler(networkHandler -> {
      networkHandler.throwException(node, cause);
    });
  }
}
