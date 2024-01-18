package org.demkiv.domain.architecture;

public interface EntityPersist <E,R>{
    R saveEntity(E entity);
    R updateEntity(E entity);
}
