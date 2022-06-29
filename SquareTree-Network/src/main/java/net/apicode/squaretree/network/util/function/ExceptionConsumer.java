package net.apicode.squaretree.network.util.function;

public interface ExceptionConsumer<T> {

  void accept(T t) throws Exception;

}
