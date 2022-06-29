package net.apicode.squaretree.server.api.event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import net.apicode.squaretree.network.util.AsyncTask;

public class EventManager {

  private HashMap<Class<? extends Event>, List<EntryMethod>> eventMethods = new HashMap<>();

  public boolean register(Method method, Listener listener) {
    if(!isMethodValid(method)) return false;
    Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
    List<EntryMethod> methods = getMethods(eventClass);
    methods.add(new EntryMethod(method, listener));
    sort(methods);
    eventMethods.put(eventClass, methods);
    return true;
  }

  public boolean isMethodValid(Method method) {
    if(!method.isAnnotationPresent(EventMethod.class)) return false;
    if(method.getParameterCount() != 1) return false;
    if(!Event.class.isAssignableFrom(method.getParameterTypes()[0])) return false;
    return !Modifier.isStatic(method.getModifiers());
  }

  public boolean unregister(Method method) {
    if(!isMethodValid(method)) return false;
    Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
    List<EntryMethod> methods = getMethods(eventClass);
    methods.removeIf(entryMethod -> entryMethod.method == method);
    sort(methods);
    eventMethods.put(eventClass, methods);
    return true;
  }

  public List<EntryMethod> getMethods(Class<? extends Event> clazz) {
    if(eventMethods.containsKey(clazz)) {
      return eventMethods.get(clazz);
    }
    return new ArrayList<>();
  }

  public void registerListener(Listener listener) {
    Class<? extends Listener> clazz = listener.getClass();
    Method[] methods = clazz.getMethods();
    for (Method method : methods) {
      register(method, listener);
    }
  }

  public void unregisterListener(Listener listener) {
    Class<? extends Listener> clazz = listener.getClass();
    Method[] methods = clazz.getMethods();
    for (Method method : methods) {
      unregister(method);
    }
  }

  public void call(Event event) {
    for (EntryMethod method : getMethods(event.getClass())) {
      try {
        method.getMethod().invoke(method.getListener(), event);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public Future<?> callAsync(Event event) {
    return AsyncTask.create(() -> call(event));
  }

  private void sort(List<EntryMethod> list) {
    list.sort((method1, method2) -> {
      short value1 = 0;
      short value2 = 0;
      if (method1.getMethod().isAnnotationPresent(EventMethod.class)) {
        value1 = method1.getMethod().getAnnotation(EventMethod.class).priority();
      }
      if (method2.getMethod().isAnnotationPresent(EventMethod.class)) {
        value2 = method2.getMethod().getAnnotation(EventMethod.class).priority();
      }
      return Short.compare(value1, value2);
    });
  }

  private static class EntryMethod {

    private final Method method;
    private final Listener listener;

    public EntryMethod(Method method, Listener listener) {
      this.method = method;
      this.listener = listener;
    }

    public Method getMethod() {
      return method;
    }

    public Listener getListener() {
      return listener;
    }
  }




}
