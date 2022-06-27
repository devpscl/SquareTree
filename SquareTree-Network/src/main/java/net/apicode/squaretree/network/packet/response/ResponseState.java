package net.apicode.squaretree.network.packet.response;

public enum ResponseState {

  SUCCESSFUL(0),
  ERROR(1),
  TOTAL_ERROR(2);

  private final int state;

  ResponseState(int state) {
    this.state = state;
  }

  public int getState() {
    return state;
  }

  public static ResponseState getState(int id) {
    if(id == 0) return SUCCESSFUL;
    if(id == 1) return ERROR;
    return TOTAL_ERROR;
  }
}
