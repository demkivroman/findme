package org.demkiv.persistance.service;

public interface PersistService<T> {
    void saveEntity(T entity, String value);
}
