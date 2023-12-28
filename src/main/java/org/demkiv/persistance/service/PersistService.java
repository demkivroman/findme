package org.demkiv.persistance.service;

public interface PersistService<T> {
    void savePerson(T finder, String photoUrl);
}
