package org.demkiv.domain.architecture;

import java.util.List;

public interface EntityFinder <I,R> {
    List<R> findEntity(I information);
}
