import net.apicode.squaretree.network.BridgeNetwork;
import net.apicode.squaretree.network.BridgeSocket;
import net.apicode.squaretree.network.NetworkException;
import net.apicode.squaretree.network.NetworkNode;
import net.apicode.squaretree.network.handler.NetworkHandler;
import net.apicode.squaretree.network.protocol.PacketNetworkPing;
import net.apicode.squaretree.network.util.ConnectionInfo;
import net.apicode.squaretree.network.util.NodeId;
import net.apicode.squaretree.network.util.SecurityInfo;

public class TestClient {

  public static void main(String[] args) throws NetworkException {
    BridgeSocket socket = new BridgeSocket(ConnectionInfo.builder(), SecurityInfo.DEFAULT,
        new NodeId("7ez8", "Bot"), new NetworkHandler() {
      @Override
      public void nodePreConnect(NetworkNode networkNode) {
        System.out.println("PreConnect");
      }

      @Override
      public void nodeClose(NetworkNode networkNode) {
        System.out.println("Close: " + networkNode.getId().toString());
      }

      @Override
      public void nodeConnect(NetworkNode networkNode) {
        System.out.println("Connect: " + networkNode.getId().toString());
      }

      @Override
      public void throwException(NetworkNode networkNode, Throwable throwable) {
        System.out.println("error:");
        throwable.printStackTrace();
      }

      @Override
      public void networkOpen(BridgeNetwork network) {
        System.out.println("open");
      }

      @Override
      public void networkClose(BridgeNetwork network) {
        System.out.println("close");
      }
    });
    long ms = System.currentTimeMillis();
    System.out.println(socket.ping() + "ms");
    System.out.println("latenz: " + (System.currentTimeMillis()-ms));

  }


}
