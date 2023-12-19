package org.demkiv.architecture;

import java.util.List;

public interface EntityFinder <I,R> {
    List<R> findEntity(I information);
}
