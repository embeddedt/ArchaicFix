package org.embeddedt.archaicfix.util;

import java.util.*;

/**
 * Simple wrapper for a list that enables changes to be observed efficiently.
 */
public class ObservableList<T> implements List<T> {
    private final List<T> backingList;
    boolean dirty = false;

    public ObservableList(List<T> backingList) {
        this.backingList = Objects.requireNonNull(backingList);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        dirty = false;
    }

    public List<T> getUnderlyingList() {
        return backingList;
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public boolean isEmpty() {
        return backingList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return backingList.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return listIterator();
    }

    @Override
    public Object[] toArray() {
        return backingList.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        return backingList.toArray(t1s);
    }

    @Override
    public boolean add(T t) {
        return setDirtyIfTrue(backingList.add(t));
    }

    private boolean setDirtyIfTrue(boolean ret) {
        if(ret) {
            dirty = true;
        }
        return ret;
    }

    @Override
    public boolean remove(Object o) {
        return setDirtyIfTrue(backingList.remove(o));
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return backingList.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return setDirtyIfTrue(backingList.addAll(collection));
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> collection) {
        return setDirtyIfTrue(backingList.addAll(i, collection));
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return setDirtyIfTrue(backingList.removeAll(collection));
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return setDirtyIfTrue(backingList.retainAll(collection));
    }

    @Override
    public void clear() {
        backingList.clear();
        dirty = true;
    }

    @Override
    public T get(int i) {
        return backingList.get(i);
    }

    @Override
    public T set(int i, T t) {
        T n = backingList.set(i, t);
        if(n != t)
            dirty = true;
        return n;
    }

    @Override
    public void add(int i, T t) {
        backingList.add(i, t);
        dirty = true;
    }

    @Override
    public T remove(int i) {
        dirty = true;
        return backingList.remove(i);
    }

    @Override
    public int indexOf(Object o) {
        return backingList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return backingList.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return new ObservableIterator<>(this, backingList.listIterator());
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        return new ObservableIterator<>(this, backingList.listIterator(i));
    }

    @Override
    public List<T> subList(int i, int i1) {
        throw new UnsupportedOperationException();
    }
}
