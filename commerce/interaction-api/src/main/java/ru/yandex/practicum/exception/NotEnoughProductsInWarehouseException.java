package ru.yandex.practicum.exception;

public class NotEnoughProductsInWarehouseException extends RuntimeException {
    public NotEnoughProductsInWarehouseException(String message) {
        super(message);
    }
}