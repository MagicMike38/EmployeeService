package com.mycompany.services;

public interface AbstractKakfaService<T> {
    boolean publish(T t);
    boolean consume();
}
