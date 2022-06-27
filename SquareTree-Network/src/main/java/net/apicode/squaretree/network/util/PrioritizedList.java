package net.apicode.squaretree.network.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PrioritizedList<T> extends ArrayList<T> {

  public PrioritizedList(Collection<? extends T> c) {
    super(c);
  }

  public PrioritizedList() {
  }

  public PrioritizedList(PrioritizedList<T> prioritizedList) {
    super(prioritizedList);
  }

  public PrioritizedList(int initialCapacity) {
    super(initialCapacity);
  }

  @Override
  public boolean add(T t) {
    boolean state = super.add(t);
    update();
    return state;
  }

  @Override
  public void add(int index, T element) {
    super.add(index, element);
    update();
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    boolean state = super.addAll(c);
    update();
    return state;
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c) {
    boolean state = super.addAll(index, c);
    update();
    return state;
  }

  @Override
  public T set(int index, T element) {
    T e = super.set(index, element);
    update();
    return e;
  }

  @Override
  public T remove(int index) {
    T element = super.remove(index);
    update();
    return element;
  }

  @Override
  protected void removeRange(int fromIndex, int toIndex) {
    super.removeRange(fromIndex, toIndex);
    update();
  }

  @Override
  public boolean remove(Object o) {
    boolean state = super.remove(o);
    update();
    return state;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    boolean state = super.removeAll(c);
    update();
    return state;
  }

  public Collection<T> copyCollection() {
    return Collections.unmodifiableCollection(this);
  }

  public void update() {
    Priority.Sorter.sortByPriority(this);
  }
}
