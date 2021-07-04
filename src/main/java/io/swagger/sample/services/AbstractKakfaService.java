package io.swagger.sample.services;

public interface AbstractKakfaService<T> {
    boolean publish(T t);
    boolean consume();
}
