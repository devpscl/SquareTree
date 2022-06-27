package net.apicode.squaretree.network.util;

public class SecurityInfo {

  public static SecurityInfo DEFAULT = new SecurityInfo("pky", null);

  private final String privateKey;
  private final String cryptoKey;

  public SecurityInfo(String privateKey, String cryptoKey) {
    this.privateKey = privateKey;
    this.cryptoKey = cryptoKey;
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public String getCryptoKey() {
    return cryptoKey;
  }

  public boolean isCryptoModeAvailable() {
    return cryptoKey != null;
  }
}
