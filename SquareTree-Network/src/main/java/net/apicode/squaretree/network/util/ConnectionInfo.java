package net.apicode.squaretree.network.util;

import java.io.IOException;
import java.net.InetAddress;
import net.apicode.squaretree.network.InvalidConnectionException;

/**
 * ConnectionInfo of hostaddress and port
 */
public interface ConnectionInfo {

  /**
   * Gets inetaddress.
   *
   * @return the hostaddress
   */
  InetAddress getAddress();

  /**
   * Gets address as string.
   *
   * @return the hostaddress as string
   */
  default String getAddressAsString() {
    return getAddress().getHostAddress();
  }

  /**
   * Gets port.
   *
   * @return the port
   */
  int getPort();

  /**
   * Build a new Connection.
   *
   * @return the builder
   */
  static Builder builder() {
    return new Builder();
  }

  /**
   * The type Builder.
   */
  class Builder implements ConnectionInfo {

    private InetAddress address;
    private int port;

    public InetAddress getAddress() {
      return address;
    }

    public int getPort() {
      return port;
    }

    /**
     * Sets address.
     *
     * @param address the inetaddress
     */
    public Builder setAddress(InetAddress address) {
      this.address = address;
      return this;
    }

    /**
     * Sets address.
     *
     * @param address the string hostaddress
     */
    public Builder setAddress(String address) {
      try {
        this.address = InetAddress.getByName(address);
      } catch (IOException e) {
        throw new InvalidConnectionException("Hostaddress: " + address);
      }
      return this;
    }

    /**
     * Sets port.
     *
     * @param port the port
     */
    public Builder setPort(int port) {
      this.port = port;
      return this;
    }

  }


}
