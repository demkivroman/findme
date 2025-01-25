package org.demkiv.domain.architecture;

public interface EntitySender <R,T> {
    R send(T entity);
}
