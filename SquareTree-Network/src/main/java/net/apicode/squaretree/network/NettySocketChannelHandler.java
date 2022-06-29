package net.apicode.squaretree.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.apicode.squaretree.network.handler.PacketReceiver;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketType;
import net.apicode.squaretree.network.protocol.PacketNetworkPing;
import net.apicode.squaretree.network.util.AsyncTask;
import net.apicode.squaretree.network.util.PacketUtil;

public class NettySocketChannelHandler extends SimpleChannelInboundHandler<Packet<?>> {

  private final BridgeSocket socket;

  public NettySocketChannelHandler(BridgeSocket socket) {
    this.socket = socket;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet) throws Exception {
    NetworkNode node = socket.getNetworkNode();
    Class<? extends Packet<?>> packetClass = (Class<? extends Packet<?>>) packet.getClass();
    if(packet.getContainerType() == PacketType.REQUEST) {
      AsyncTask.create(() -> {
        if(acceptPacket(packet, node)) {
          for (PacketReceiver<?> packetListener : socket.getPacketListeners(packetClass)) {
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
      socket.getWaitingQueue().forwardPacket(packet);
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
    socket.foreachHandler(networkHandler -> {
      networkHandler.throwException(socket.getNetworkNode(), cause);
    });
  }
}
