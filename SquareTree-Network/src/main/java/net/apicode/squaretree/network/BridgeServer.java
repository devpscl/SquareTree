package net.apicode.squaretree.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
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
import java.util.Collection;
import java.util.concurrent.Future;
import net.apicode.squaretree.network.handler.NetworkHandler;
import net.apicode.squaretree.network.packet.Packet;
import net.apicode.squaretree.network.packet.PacketDecoder;
import net.apicode.squaretree.network.packet.PacketEncoder;
import net.apicode.squaretree.network.packet.response.PingResponse;
import net.apicode.squaretree.network.packet.response.Response;
import net.apicode.squaretree.network.protocol.PacketNetworkPing;
import net.apicode.squaretree.network.util.AsyncTask;
import net.apicode.squaretree.network.util.ConnectionInfo;
import net.apicode.squaretree.network.util.NodeId;
import net.apicode.squaretree.network.util.SecurityInfo;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The type Bridge server.
 * Create a new bridge server
 */
public class BridgeServer extends BridgeNetwork {

  private static final boolean epoll = Epoll.isAvailable();

  private final EventLoopGroup bossGroup = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
  private final EventLoopGroup workerGroup = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();

  private final ServerBootstrap bootstrap;

  private ChannelFuture channelFuture;
  private NetworkNode networkNode;
  private final NodeMap nodeMap = new NodeMap();

  /**
   * Instantiates a new Bridge server.
   *
   * This constructer will be start the server
   *
   * @param connectionInfo the connection info
   * @throws NetworkException the network exception
   */
  public BridgeServer(@NotNull ConnectionInfo connectionInfo) throws NetworkException {
    this(connectionInfo, SecurityInfo.DEFAULT);
  }

  /**
   * Instantiates a new Bridge server
   *
   * @param connectionInfo the connection info
   * @param securityInfo   the security info
   * @param handlers       the handlers to register before start server
   * @throws NetworkException the network exception
   */
  public BridgeServer(@NotNull ConnectionInfo connectionInfo,
      @NotNull SecurityInfo securityInfo, NetworkHandler...handlers) throws NetworkException {
    super(connectionInfo, securityInfo);
    for (NetworkHandler handler : handlers) {
      addHandler(handler);
    }
    bootstrap = new ServerBootstrap();
    bootstrap.group(bossGroup, workerGroup)
        .channel(epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel socketChannel) {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast(new PacketDecoder(BridgeServer.this, securityInfo));
            pipeline.addLast(new PacketEncoder(BridgeServer.this, securityInfo));

            pipeline.addLast(new NettyServerChannelHandler(BridgeServer.this));
          }
        });

  }

  /**
   * Start the server
   */
  public void start() throws NetworkException {
    try {
      this.channelFuture = bootstrap.bind(getConnectionInfo().getAddress(), getConnectionInfo().getPort()).sync();
      networkNode = new NetworkNode(this, channelFuture.channel(), NodeId.SERVER);
      foreachHandler(networkHandler -> {
        networkHandler.networkOpen(this);
      });
    } catch (InterruptedException e) {
      throw new NetworkException(e);
    }
  }

  /**
   * Gets network node of channel.
   * If node doesn't exists return null
   *
   * @param channel the channel
   * @return the node
   */
  @Nullable
  public NetworkNode getNode(Channel channel) {
    return getNodeMap().getNode(channel);
  }

  /**
   * Gets network node of node id.
   * If node doesn't exists return null
   *
   * @param nodeId the node id
   * @return the node
   */
  @Nullable
  public NetworkNode getNode(NodeId nodeId) {
    return getNodeMap().getNode(nodeId);
  }

  /**
   * Gets node map.
   * Method is internal
   * @return the node map
   */
  @Internal
  protected NodeMap getNodeMap() {
    return nodeMap;
  }

  @NotNull
  public NetworkNode getNetworkNode() {
    return networkNode;
  }

  /**
   * Waiting of server and automatic close of service groups
   *
   * @throws NetworkException the network exception
   */
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

  /**
   * Waiting of server async and automatic close of service groups
   *
   */
  public Future<?> scheduleAsync() {
    return AsyncTask.create(() -> {
      try {
        schedule();
      } catch (NetworkException e) {
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * Check ping of sending and receiving packet to bridge socket
   *
   * @return the ping as long
   * @throws NetworkException the network exception
   */
  public long ping(@NotNull NodeId node) throws NetworkException {
    PacketNetworkPing packet = new PacketNetworkPing();
    PingResponse pingResponse = sendPacket(packet, node);
    return pingResponse.getValue();
  }

  /**
   * Close server connection
   */
  public void close() {
    channelFuture.channel().close();
    foreachHandler(networkHandler -> {
      networkHandler.networkClose(this);
    });
  }

  /**
   * @return collection of node ids
   */
  public Collection<NodeId> getNodeIds() {
    return getNodeMap().getNodeIds();
  }

  /**
   * <p style="color:red">It will be not supported for server</p>
   */
  @Override
  @Internal
  @Deprecated
  public <T extends Response<?>> T sendPacket(Packet<T> packet) {
    throw new UnsupportedOperationException("You must be have a target");
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
  public <T extends Response<?>> T sendPacket(@NotNull Packet<T> packet, @NotNull  NodeId target)
      throws NetworkException {
    packet.setNodeInformation(networkNode.getId(), target);
    NetworkNode node = nodeMap.getNode(target);
    String packetId = packet.getPacketId();
    getWaitingQueue().openEntry(packetId);
    sendPacket(packet, node.getChannel());
    Packet<?> resultPacket = getWaitingQueue().waitOfPacket(packetId);
    return (T) resultPacket.getResponse();
  }

  /**
   * Send packet asynchronous to specific target.
   * If target no exists will be return error response
   *
   * @param <T>    the response type
   * @param packet the packet
   * @return the future of response packet
   */
  public <T extends Response<?>> Future<T> sendPacketAsync(@NotNull Packet<T> packet, @NotNull  NodeId target) {
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
