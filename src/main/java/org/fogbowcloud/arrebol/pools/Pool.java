package org.fogbowcloud.arrebol.pools;

public interface Pool<E> {
    boolean addToPool(E e);
}
