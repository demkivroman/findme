package org.demkiv.architecture;

import java.util.List;

public interface EntitySupplierSingle<T, R> {
    List<R> retrieveEntity(T inputValue);
}
