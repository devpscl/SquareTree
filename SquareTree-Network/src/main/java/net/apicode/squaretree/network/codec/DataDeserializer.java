package net.apicode.squaretree.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.Charset;
import net.apicode.squaretree.network.codec.converter.DataConverter;

/**
 * Deserialize byte array
 */
public class DataDeserializer {

  private final ByteBuf byteBuf;

  /**
   * Instantiates a new Data deserializer.
   *
   * @param byteBuf the byte buf
   */
  public DataDeserializer(ByteBuf byteBuf) {
    this.byteBuf = byteBuf;
  }

  /**
   * Instantiates a new Data deserializer.
   *
   * @param bytes the bytes
   */
  public DataDeserializer(byte[] bytes) {
    this.byteBuf = Unpooled.wrappedBuffer(bytes);
  }

  /**
   * Read short.
   *
   * @return the value
   */
  public short readShort() {
    return byteBuf.readShort();
  }

  /**
   * Read short.
   *
   * @return the unsigned value
   */
  public int readUnsignedShort() {
    return byteBuf.readUnsignedShort();
  }

  /**
   * Read int.
   *
   * @return the value
   */
  public int readInt() {
    return byteBuf.readInt();
  }

  /**
   * Read int.
   *
   * @return the unsigned value
   */
  public long readUnsignedInt() {
    return byteBuf.readUnsignedInt();
  }

  /**
   * Read long.
   *
   * @return the value
   */
  public long readLong() {
    return byteBuf.readLong();
  }

  /**
   * Read float.
   *
   * @return the value
   */
  public float readFloat() {
    return byteBuf.readFloat();
  }

  /**
   * Read double.
   *
   * @return the value
   */
  public double readDouble() {
    return byteBuf.readDouble();
  }

  /**
   * Read boolean.
   *
   * @return the value
   */
  public boolean readBoolean() {
    return byteBuf.readBoolean();
  }

  /**
   * Read character.
   *
   * @return the value
   */
  public char readChar() {
    return byteBuf.readChar();
  }

  /**
   * Read one byte.
   *
   * @return the value
   */
  public byte readByte() {
    return byteBuf.readByte();
  }

  /**
   * Read limited byte array.
   *
   * @return the value
   */
  public byte[] readBytes() {
    int varInt = readInt();
    byte[] bytes = new byte[varInt];
    byteBuf.readBytes(bytes);
    return bytes;
  }

  /**
   * Read string with default encoding.
   *
   * @return the string
   */
  public String readString() {
    return new String(readBytes());
  }

  /**
   * Read string with custom encoding.
   *
   * @return the string
   */
  public String readString(Charset charset) {
    return new String(readBytes(), charset);
  }

  /**
   * Read data by converter.
   *
   * @param <T>           the type parameter
   * @param dataConverter the data converter
   * @return the deserialized object
   */
  public <T> T readData(DataConverter<T> dataConverter) {
    try {
      return dataConverter.deserialize(readContainer());
    } catch (Exception e) {
      throw new NetworkCodecException("Failed to deserialize data", e);
    }
  }

  /**
   * Read deserializer of bytes
   *
   * @return the data deserializer
   */
  public DataDeserializer readContainer() {
    return new DataDeserializer(readBytes());
  }

  /**
   * Read data container
   */
  public void readContainer(DataContainer dataContainer) {
    DataDeserializer deserializer = readContainer();
    dataContainer.deserialize(deserializer);
  }

  /**
   * Read bytebuf of bytes
   *
   * @return the byte buf
   */
  public ByteBuf readByteBuf() {
    return Unpooled.wrappedBuffer(readBytes());
  }

  /**
   * Convert deserializer to bytebuf
   *
   * @return the byte buf
   */
  public ByteBuf asByteBuf() {
    return Unpooled.copiedBuffer(byteBuf);
  }

  /**
   * Get bytes
   *
   * @return the byte array
   */
  public byte[] array() {
    return byteBuf.array();
  }



}
