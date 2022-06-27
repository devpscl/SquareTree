package net.apicode.squaretree.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.apicode.squaretree.network.packet.PacketDecoder;
import net.apicode.squaretree.network.packet.PacketEncoder;
import net.apicode.squaretree.network.util.ConnectionInfo;
import net.apicode.squaretree.network.util.NodeId;
import net.apicode.squaretree.network.util.SecurityInfo;
import org.jetbrains.annotations.NotNull;

public class BridgeServer extends BridgeNetwork {

  private static final boolean epoll = Epoll.isAvailable();

  private final EventLoopGroup bossGroup = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
  private final EventLoopGroup workerGroup = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();

  private final ChannelFuture channelFuture;
  private final NetworkNode networkNode;

  public BridgeServer(@NotNull ConnectionInfo connectionInfo,
      @NotNull SecurityInfo securityInfo, @NotNull NodeId serverNodeId) throws NetworkException {
    super(connectionInfo, securityInfo);
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(bossGroup, workerGroup)
        .channel(epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel socketChannel) {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast(new PacketDecoder(BridgeServer.this));
            pipeline.addLast(new PacketEncoder(BridgeServer.this));

            pipeline.addLast(new NettyServerChannelHandler(BridgeServer.this));
          }
        });
    try {
      this.channelFuture = bootstrap.bind(connectionInfo.getPort()).sync();
      networkNode = new NetworkNode(this, channelFuture.channel(), serverNodeId);
    } catch (InterruptedException e) {
      throw new NetworkException(e);
    }
  }

  public NetworkNode getNetworkNode() {
    return networkNode;
  }

  public void schedule() throws NetworkException {
    try {
      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      throw new NetworkException(e);
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
