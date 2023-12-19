package org.demkiv.architecture;

import java.util.List;

public interface EntitySupplier <T,R> {
    List<R> retrieveAllEntities();
    List<R> retrieveEntity(T inputValue);
}
