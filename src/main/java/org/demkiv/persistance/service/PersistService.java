package org.demkiv.persistance.service;

public interface PersistService<T, R> {
    R saveEntity(T entity);
}
