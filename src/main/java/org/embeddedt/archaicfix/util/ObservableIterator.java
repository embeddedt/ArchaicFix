package org.embeddedt.archaicfix.util;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.function.Consumer;

public class ObservableIterator<T> implements ListIterator<T> {
    private final ListIterator<T> backingIterator;
    private final ObservableList<T> list;

    public ObservableIterator(ObservableList<T> list, ListIterator<T> i) {
        this.list = list;
        this.backingIterator = i;
    }

    @Override
    public boolean hasNext() {
        return backingIterator.hasNext();
    }

    @Override
    public T next() {
        return backingIterator.next();
    }

    @Override
    public boolean hasPrevious() {
        return backingIterator.hasPrevious();
    }

    @Override
    public T previous() {
        return backingIterator.previous();
    }

    @Override
    public int nextIndex() {
        return backingIterator.nextIndex();
    }

    @Override
    public int previousIndex() {
        return backingIterator.previousIndex();
    }

    @Override
    public void remove() {
        backingIterator.remove();
        list.dirty = true;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> consumer) {
        backingIterator.forEachRemaining(consumer);
    }

    @Override
    public void set(T t) {
        backingIterator.set(t);
        list.dirty = true;
    }

    @Override
    public void add(T t) {
        backingIterator.add(t);
        list.dirty = true;
    }
}
