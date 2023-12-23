package org.demkiv.domain.architecture;

public interface EntitySaver<E,R> {
    R saveEntity(E entity);
}
