package net.apicode.squaretree.network.util;

import java.util.Arrays;
import java.util.Random;

public class IdGenerator {

  private static final Random RANDOM = new Random();

  private final int length;
  private final char[] characters;

  public IdGenerator(int length, char[] characters) {
    this.length = length;
    this.characters = characters;
  }

  public IdGenerator(Builder builder) {
    this.length = builder.length;
    this.characters = builder.chars;
  }

  public int getLength() {
    return length;
  }

  public char[] getCharacters() {
    return characters;
  }

  public char[] generateId() {
    return generateId(RANDOM);
  }

  public char[] generateId(long seed) {
    return generateId(new Random(seed));
  }

  public char[] generateId(Random random) {
    char[] chars = new char[length];
    for (int i = 0; i < length; i++) {
      chars[i] = characters[random.nextInt(characters.length)];
    }
    return chars;
  }

  public String generateIdToString() {
    return new String(generateId());
  }

  public String generateIdToString(long seed) {
    return new String(generateId(seed));
  }

  public String generateIdToString(Random random) {
    return new String(generateId(random));
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private char[] chars = new char[]{};
    private int length = 0;

    public Builder length(int i) {
      this.length = i;
      return this;
    }

    public Builder withLowercaseCharacters() {
      char[] array = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
          'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
      chars = concatWithArrayCopy(chars, array);
      return this;
    }

    public Builder withUppercaseCharacters() {
      char[] array = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
          'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
      chars = concatWithArrayCopy(chars, array);
      return this;
    }

    public Builder withNumericCharacters() {
      char[] array = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
      chars = concatWithArrayCopy(chars, array);
      return this;
    }

    public Builder withSymbolsCharacters() {
      char[] array = new char[]{'#', '$', '%', '&', '(', ')', '*', '+', ',', '-', '.', ':', ';', '<', '=', '>',
          '?', '@', '{', '|', '}', '~'};
      chars = concatWithArrayCopy(chars, array);
      return this;
    }

    public Builder withEscapeSymbolsCharacters() {
      char[] array = new char[]{'', '', '', '', '', '', '', '', '', '', '', '',
          '', '', '', '', '', '', '', '', '', '', '', '', ''};
      chars = concatWithArrayCopy(chars, array);
      return this;
    }

    public Builder withOthersSymbolsCharacters() {
      char[] array = new char[]{'Ҧ', 'ҧ', 'Ҩ', 'ҩ', 'Ҫ', 'ҫ', 'Ҭ', 'ҭ', 'Ү', 'ү', 'Ұ', 'ұ', 'Ҳ', 'ҳ', 'Ҵ', 'ҵ',
          'Ҷ', 'ҷ', 'Ҹ', 'ҹ', 'Һ', 'һ', 'Ҽ', 'ҽ', 'Ҿ', 'ҿ', 'Ӏ', 'Ӂ', 'ӂ', 'Ӄ', 'ӄ', 'Ӆ', 'ӆ', 'Ӈ', 'ӈ', 'Ӊ',
          'ӊ', 'Ӌ', 'ӌ', 'Ӎ', 'ӎ', 'ӏ', 'Ӑ', 'ӑ', 'Ӓ', 'ӓ', 'Ӕ', 'ӕ', 'Ӗ', 'ӗ', 'Ә', 'ә', 'Ӛ', 'ӛ', 'Ӝ', 'ӝ',
          'Ӟ', 'ӟ', 'Ӡ', 'ӡ', 'Ӣ', 'ӣ', 'Ӥ', 'ӥ', 'Ӧ', 'ӧ', 'Ө', 'ө', 'Ӫ', 'ӫ', 'Ӭ', 'ӭ', 'Ӯ', 'ӯ', 'Ӱ', 'ӱ',
          'Ӳ'};
      chars = concatWithArrayCopy(chars, array);
      return this;
    }

    public IdGenerator build() {
      return new IdGenerator(length, chars);
    }

  }

  private static char[] concatWithArrayCopy(char[] array1, char[] array2) {
    char[] result = Arrays.copyOf(array1, array1.length + array2.length);
    System.arraycopy(array2, 0, result, array1.length, array2.length);
    return result;
  }

}
