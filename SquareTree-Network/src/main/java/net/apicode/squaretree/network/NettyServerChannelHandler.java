package net.apicode.squaretree.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.apicode.squaretree.network.packet.Packet;

public class NettyServerChannelHandler extends SimpleChannelInboundHandler<Packet<?>> {

  private final BridgeServer bridgeServer;

  public NettyServerChannelHandler(BridgeServer bridgeServer) {
    this.bridgeServer = bridgeServer;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet) throws Exception {

  }
}
