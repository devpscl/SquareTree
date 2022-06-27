package net.apicode.squaretree.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.Charset;
import net.apicode.squaretree.network.codec.converter.DataConverter;

/**
 * Serialize data to bytes
 */
public class DataSerializer {

  private final ByteBuf byteBuf;

  /**
   * Instantiates a new Data serializer.
   * Default capacity is 32.
   */
  public DataSerializer() {
    this(32);
  }

  /**
   * Instantiates a new Data serializer.
   *
   * @param capacity the capacity
   */
  public DataSerializer(int capacity) {
    byteBuf = Unpooled.buffer(capacity);
  }

  /**
   * Instantiates a new Data serializer.
   *
   * @param byteBuf the bytebuf
   */
  public DataSerializer(ByteBuf byteBuf) {
    this.byteBuf = byteBuf;
  }



  /**
   * Write short.
   *
   * @param value the value
   */
  public void writeShort(int value) {
    byteBuf.writeShort(value);
  }

  /**
   * Write int.
   *
   * @param value the value
   */
  public void writeInt(int value) {
    byteBuf.writeInt(value);
  }

  /**
   * Write long.
   *
   * @param value the value
   */
  public void writeLong(long value) {
    byteBuf.writeLong(value);
  }

  /**
   * Write float.
   *
   * @param value the value
   */
  public void writeFloat(float value) {
    byteBuf.writeFloat(value);
  }

  /**
   * Write double.
   *
   * @param value the value
   */
  public void writeDouble(double value) {
    byteBuf.writeDouble(value);
  }

  /**
   * Write boolean.
   *
   * @param value the value
   */
  public void writeBoolean(boolean value) {
    byteBuf.writeBoolean(value);
  }

  /**
   * Write char.
   *
   * @param value the value
   */
  public void writeChar(int value) {
    byteBuf.writeChar(value);
  }

  /**
   * Write byte.
   *
   * @param value the value
   */
  public void writeByte(int value) {
    byteBuf.writeByte(value);
  }

  /**
   * Write bytes.
   *
   * @param value the value
   */
  public void writeBytes(byte[] value) {
    byteBuf.writeInt(value.length);
    byteBuf.writeBytes(value);
  }

  /**
   * Write string with default encoding.
   *
   * @param value the value
   */
  public void writeString(String value) {
    writeBytes(value.getBytes());
  }

  /**
   * Write string with custom encoding.
   *
   * @param value   the value
   * @param charset the charset
   */
  public void writeString(String value, Charset charset) {
    writeBytes(value.getBytes(charset));
  }

  /**
   * Write data by converter
   *
   * @param <T>       the type parameter
   * @param value     the value to serialize
   * @param converter the converter
   */
  public <T> void writeData(T value, DataConverter<T> converter) {
    try {
      writeContainer(converter.serialize(value));
    } catch (Exception e) {
      throw new NetworkCodecException("Failed to write data", e);
    }
  }

  /**
   * Write bytebuf as bytes
   *
   * @param byteBuf the netty bytebuf
   */
  public void writeByteBuf(ByteBuf byteBuf) {
    writeBytes(byteBuf.array());
  }

  /**
   * Write data serializer as bytes.
   *
   * @param serializer the serializer
   */
  public void writeContainer(DataSerializer serializer) {
    writeBytes(serializer.array());
  }

  /**
   * Write data container as bytes.
   *
   * @param container the data container
   */
  public void writeContainer(DataContainer container) {
    DataSerializer serializer = new DataSerializer(container.getBufferCapacity());
    container.serialize(serializer);
    writeContainer(serializer);
  }

  /**
   * Convert serializer to bytebuf
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
