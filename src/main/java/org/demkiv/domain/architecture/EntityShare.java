package org.demkiv.domain.architecture;

public interface EntityShare <T, R> {
    R postEntity(T inputValue);
}
