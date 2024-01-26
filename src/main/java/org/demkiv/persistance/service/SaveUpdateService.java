package org.demkiv.persistance.service;

public interface SaveUpdateService<T,R> {
    R saveEntity(T entity);
    R updateEntity(T entity);
}
