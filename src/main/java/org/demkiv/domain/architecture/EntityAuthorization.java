package org.demkiv.domain.architecture;

public interface EntityAuthorization<T, R> {
    R authorize(T inputValue);
}
