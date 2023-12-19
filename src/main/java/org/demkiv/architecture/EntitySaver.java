package org.demkiv.architecture;

public interface EntitySaver<E,R> {
    R saveEntity(E entity);
}
