package org.demkiv.persistance.service;

public interface SaveUpdateService<T> {
    void saveEntity(T entity);
    void updateEntity(T entity);
}
