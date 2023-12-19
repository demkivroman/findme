package org.demkiv.architecture;

public interface EntityShare <T, R> {
    R postEntity(T inputValue);
}
