package net.apicode.squaretree.network.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncTask {

  private static final ExecutorService executorService = Executors.newCachedThreadPool();

  public static Future<?> create(Runnable runnable) {
    return executorService.submit(runnable);
  }

  public static <T> Future<T> create(Callable<T> callable) {
    return executorService.submit(callable);
  }

  public static ExecutorService getExecutorService() {
    return executorService;
  }
}
