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
import java.util.concurrent.Future;
import net.apicode.squaretree.network.handler.NetworkHandler;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketDecoder;
import net.apicode.squaretree.network.packet.PacketEncoder;
import net.apicode.squaretree.network.packet.response.PingResponse;
import net.apicode.squaretree.network.packet.response.RegisterResponse;
import net.apicode.squaretree.network.packet.response.Response;
import net.apicode.squaretree.network.protocol.PacketNetworkPing;
import net.apicode.squaretree.network.protocol.PacketNetworkRegister;
import net.apicode.squaretree.network.util.AsyncTask;
import net.apicode.squaretree.network.util.ConnectionInfo;
import net.apicode.squaretree.network.util.NodeId;
import net.apicode.squaretree.network.util.SecurityInfo;
import org.jetbrains.annotations.NotNull;

/**
 * The type Bridge socket.
 * Create a new bridge socket connection
 */
public class BridgeSocket extends BridgeNetwork {

  private static final boolean epoll = Epoll.isAvailable();

  private final EventLoopGroup group = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();

  private final NetworkNode networkNode;
  private final ChannelFuture channelFuture;

  /**
   * Instantiates a new Bridge socket.
   *
   * This constructer will connect to the target server
   *
   * @param connectionInfo the connection info
   * @throws NetworkException the network exception
   */
  public BridgeSocket(@NotNull ConnectionInfo connectionInfo, @NotNull NodeId nodeId) throws NetworkException {
    this(connectionInfo, SecurityInfo.DEFAULT, nodeId);
  }

  /**
   * Instantiates a new Bridge socket.
   *
   * This constructer will be connect to the target server
   *
   * @param connectionInfo the connection info
   * @param securityInfo   the security info
   * @param nodeId         the node id
   * @param handlers       the handlers to register before connecting
   * @throws NetworkException the network exception
   */
  public BridgeSocket(
      @NotNull ConnectionInfo connectionInfo,
      @NotNull SecurityInfo securityInfo, @NotNull NodeId nodeId, NetworkHandler...handlers) throws NetworkException {
    super(connectionInfo, securityInfo);

    for (NetworkHandler handler : handlers) {
      addHandler(handler);
    }
    Bootstrap bootstrap = new Bootstrap()
        .group(group)
        .channel(epoll ? EpollSocketChannel.class : NioSocketChannel.class)
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast(new PacketDecoder(BridgeSocket.this, securityInfo));
            pipeline.addLast(new PacketEncoder(BridgeSocket.this, securityInfo));

            pipeline.addLast(new NettySocketChannelHandler(BridgeSocket.this));
          }
        });
    try {
      channelFuture = bootstrap.connect(connectionInfo.getAddress(), connectionInfo.getPort()).sync();
      networkNode = new NetworkNode(this, channelFuture.channel(), nodeId);
      foreachHandler(networkHandler -> {
        networkHandler.networkOpen(this);
      });

      PacketNetworkRegister startPacket = new PacketNetworkRegister(securityInfo.getPrivateKey());
      RegisterResponse response = sendPacket(startPacket);
      switch (response.getValue()) {
        case NODE_ALREADY_LOADED:
          close();
          throw new NetworkException("Node is already exists: " + nodeId.getName());
        case INVALID_PRIVATE_KEY:
          throw new NetworkException("Invalid private key");
        case ERROR:
          throw new NetworkException("Error");
        default:
          break;
      }
      foreachHandler(networkHandler -> {
        networkHandler.nodeConnect(networkNode);
      });
    } catch (InterruptedException e) {
      throw new NetworkException(e);
    }
  }

  /**
   * Waiting of socket and automatic close of service groups
   *
   * @throws NetworkException the network exception
   */
  public void schedule() throws NetworkException {
    try {
      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      throw new NetworkException(e);
    } finally {
      group.shutdownGracefully();
    }
  }

  /**
   * Waiting of socket async and automatic close of service groups
   *
   */
  public Future<?> scheduleAsync() {
    return AsyncTask.create(new Runnable() {
      @Override
      public void run() {
        try {
          schedule();
        } catch (NetworkException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }

  /**
   * Check ping of sending and receiving packet to bridge server
   *
   * @return the ping as long
   * @throws NetworkException the network exception
   */
  public long ping() throws NetworkException {
    PacketNetworkPing packet = new PacketNetworkPing();
    PingResponse pingResponse = sendPacket(packet);
    return pingResponse.getValue();
  }

  /**
   * Close socket connection to server
   */
  public void close() {
    channelFuture.channel().close();
    foreachHandler(networkHandler -> {
      networkHandler.networkClose(this);
    });
  }

  @Override
  public NetworkNode getNetworkNode() {
    return networkNode;
  }

  /**
   * Send packet to server.
   *
   * @param <T>    the response type
   * @param packet the packet
   * @return the response of packet
   */
  @Override
  public <T extends Response<?>> T sendPacket(@NotNull Packet<T> packet) throws NetworkException {
    return sendPacket(packet, NodeId.SERVER);
  }

  /**
   * Send packet asynchronous to server.
   *
   * @param <T>    the response type
   * @param packet the packet
   * @return the future of response packet
   */
  public <T extends Response<?>> Future<T> sendPacketAsync(@NotNull Packet<T> packet) {
    return AsyncTask.create(() -> sendPacket(packet));
  }

  /**
   * Send packet to specific target.
   * If target no exists will be return a error response
   *
   * @param <T>    the response type
   * @param packet the packet
   * @return the response of packet
   */
  @Override
  public <T extends Response<?>> T sendPacket(@NotNull Packet<T> packet, @NotNull NodeId target)
      throws NetworkException {
    packet.setNodeInformation(networkNode.getId(), target);
    String packetId = packet.getPacketId();
    getWaitingQueue().openEntry(packetId);
    sendPacket(packet, networkNode.getChannel());
    Packet<?> resultPacket = getWaitingQueue().waitOfPacket(packetId);
    return (T) resultPacket.getResponse();
  }

  /**
   * Send packet async future.
   *
   * @param <T>    the type parameter
   * @param packet the packet
   * @param target the target
   * @return the future
   */
  public <T extends Response<?>> Future<T> sendPacketAsync(@NotNull Packet<T> packet, @NotNull NodeId target) {
    return AsyncTask.create(() -> sendPacket(packet, target));
  }

  /**
   * Send packet to channel without standard packet setup
   * The packet must have sender and target
   *
   * @param packet the packet
   * @param channel the target channel
   */
  @Override
  public void sendPacket(@NotNull Packet<?> packet, @NotNull Channel channel) {
    channel.writeAndFlush(packet);
  }
}
