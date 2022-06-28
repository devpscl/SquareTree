package net.apicode.squaretree.network.util;

import java.util.Objects;

public class NodeId {

  public static final NodeId SERVER = new NodeId("SERVER", "server");

  private final String sessionId;
  private final String name;

  public NodeId(String sessionId, String name) {
    this.sessionId = sessionId;
    this.name = name;
  }

  public String getSessionId() {
    return sessionId;
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NodeId that = (NodeId) o;
    if (!Objects.equals(sessionId, that.sessionId)) {
      return false;
    }
    return Objects.equals(name, that.name);
  }

  @Override
  public String toString() {
    return name  + ":" + sessionId;
  }
}
