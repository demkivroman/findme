package org.demkiv.architecture;

public interface EntityAuthorization<T, R> {
    R authorize(T inputValue);
}
