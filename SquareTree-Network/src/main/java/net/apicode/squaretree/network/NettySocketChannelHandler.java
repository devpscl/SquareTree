package net.apicode.squaretree.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.apicode.squaretree.network.handler.PacketReceiver;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketType;
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
      for (PacketReceiver<?> packetListener : socket.getPacketListeners(packetClass)) {
        try {
          packetListener.input(packet, node);
        } catch (Throwable t) {
          exceptionCaught(channelHandlerContext, t);
        }
      }
      PacketUtil.createCallback(node, packet);
    } else if(packet.getContainerType() == PacketType.RESPONSE) {
      socket.getWaitingQueue().forwardPacket(packet);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    socket.foreachHandler(networkHandler -> {
      networkHandler.throwException(socket.getNetworkNode(), cause);
    });
  }
}
