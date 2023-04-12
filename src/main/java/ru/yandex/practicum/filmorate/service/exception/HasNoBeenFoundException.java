package ru.yandex.practicum.filmorate.service.exception;

public class HasNoBeenFoundException extends RuntimeException {
    public HasNoBeenFoundException() {
        super("The object has no been Found");
    }
}
