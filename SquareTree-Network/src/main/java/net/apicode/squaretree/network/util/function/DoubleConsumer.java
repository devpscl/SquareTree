package net.apicode.squaretree.network.util.function;

@FunctionalInterface
public interface DoubleConsumer<T1, T2> {

  void accept(T1 a, T2 b);

}
