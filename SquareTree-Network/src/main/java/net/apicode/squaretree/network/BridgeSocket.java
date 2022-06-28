package net.apicode.squaretree.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketDecoder;
import net.apicode.squaretree.network.packet.PacketEncoder;
import net.apicode.squaretree.network.packet.response.RegisterResponse;
import net.apicode.squaretree.network.protocol.PacketNetworkRegister;
import net.apicode.squaretree.network.util.ConnectionInfo;
import net.apicode.squaretree.network.util.NodeId;
import net.apicode.squaretree.network.util.SecurityInfo;
import org.jetbrains.annotations.NotNull;

public class BridgeSocket extends BridgeNetwork {

  private static final boolean epoll = Epoll.isAvailable();
  private final EventLoopGroup group = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();

  private final NetworkNode networkNode;
  private final ChannelFuture channelFuture;

  public BridgeSocket(
      @NotNull ConnectionInfo connectionInfo,
      @NotNull SecurityInfo securityInfo, @NotNull NodeId nodeId) throws NetworkException {
    super(connectionInfo, securityInfo);

    Bootstrap bootstrap = new Bootstrap()
        .group(group)
        .channel(epoll ? EpollSocketChannel.class : NioSocketChannel.class)
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast(new PacketDecoder(BridgeSocket.this));
            pipeline.addLast(new PacketEncoder(BridgeSocket.this));

            pipeline.addLast(new NettySocketChannelHandler(BridgeSocket.this));
          }
        });
    try {
      channelFuture = bootstrap.bind(connectionInfo.getAddress(), connectionInfo.getPort()).sync();
      networkNode = new NetworkNode(this, channelFuture.channel(), nodeId);
      PacketNetworkRegister startPacket = new PacketNetworkRegister(securityInfo.getPrivateKey());
      RegisterResponse response = sendPacket(startPacket);
      switch (response.getValue()) {
        case NODE_NAME_ALREADY_LOADED:
          close();
          throw new NetworkException("Node name is already exists: " + nodeId.getName());
        case INVALID_PRIVATE_KEY:
          throw new NetworkException("Invalid private key");
        case ERROR:
          throw new NetworkException("Error");
        default:
          break;
      }
    } catch (InterruptedException e) {
      throw new NetworkException(e);
    }
  }

  public void schedule() throws NetworkException {
    try {
      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      throw new NetworkException(e);
    } finally {
      group.shutdownGracefully();
    }
  }

  public void close() {
    channelFuture.channel().close();
  }

  @Override
  public NetworkNode getNetworkNode() {
    return networkNode;
  }

  @Override
  public <T> T sendPacket(Packet<?> packet) throws NetworkException {
    return sendPacket(packet, NodeId.SERVER);
  }

  @Override
  public <T> T sendPacket(Packet<?> packet, NodeId target) throws NetworkException {
    packet.setNodeInformation(networkNode.getId(), target);
    String packetId = packet.getPacketId();
    getWaitingQueue().openEntry(packetId);
    sendPacket(packet, networkNode.getChannel());
    Packet<?> resultPacket = getWaitingQueue().waitOfPacket(packetId);
    return (T) resultPacket.getResponse();
  }

  @Override
  public void sendPacket(Packet<?> packet, Channel channel) {
    channel.writeAndFlush(channel);
  }
}
