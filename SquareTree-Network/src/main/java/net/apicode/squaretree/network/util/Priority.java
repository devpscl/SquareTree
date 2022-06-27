package net.apicode.squaretree.network.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Priority {

  short LOWEST = -128;
  short LOW = -64;
  short NORMAL = 0;
  short HIGH = 64;
  short HIGHEST = 128;

  short priority() default 0;

  class Sorter {

    public static <T> void sortByPriority(List<T> handlers) {
      handlers.sort((o1, o2) -> {
        Class<?> handlerClass1 = o1.getClass();
        Class<?> handlerClass2 = o2.getClass();
        short value1 = 0;
        short value2 = 0;
        if (handlerClass1.isAnnotationPresent(Priority.class)) {
          value1 = handlerClass1.getAnnotation(Priority.class).priority();
        }
        if (handlerClass2.isAnnotationPresent(Priority.class)) {
          value2 = handlerClass2.getAnnotation(Priority.class).priority();
        }
        return Short.compare(value1, value2);
      });
    }


  }

}
