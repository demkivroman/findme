package org.demkiv.domain.architecture;

public interface EntityFinder <I,R> {
    R findEntity(I information);
}
