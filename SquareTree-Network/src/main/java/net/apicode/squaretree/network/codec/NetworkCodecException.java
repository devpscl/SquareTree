package net.apicode.squaretree.network.codec;

public class NetworkCodecException extends RuntimeException {

  public NetworkCodecException() {
  }

  public NetworkCodecException(String message) {
    super(message);
  }

  public NetworkCodecException(String message, Throwable cause) {
    super(message, cause);
  }

  public NetworkCodecException(Throwable cause) {
    super(cause);
  }

  public NetworkCodecException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
