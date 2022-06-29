package net.apicode.squaretree.network.util;

import java.util.Objects;

/**
 * The type Node id.
 */
public class NodeId {

  /**
   * The default node id of the server
   */
  public static final NodeId SERVER = new NodeId("SERVER", "server");

  private final String subName;
  private final String name;

  /**
   * Instantiates a new Node id.
   *
   * @param name    the name
   * @param subName the second name/datat
   */
  public NodeId(String name, String subName) {
    this.subName = subName;
    this.name = name;
  }

  /**
   * Gets second name / data.
   *
   * @return the second name
   */
  public String getSubName() {
    return subName;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    int result = subName != null ? subName.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
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
    if (!Objects.equals(subName, that.subName)) {
      return false;
    }
    return Objects.equals(name, that.name);
  }

  @Override
  public String toString() {
    return name  + ":" + subName;
  }

}
