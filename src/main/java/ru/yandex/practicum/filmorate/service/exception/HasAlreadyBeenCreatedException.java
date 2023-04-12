package ru.yandex.practicum.filmorate.service.exception;

public class HasAlreadyBeenCreatedException extends RuntimeException {
    public HasAlreadyBeenCreatedException() {
        super("The object has already been created");
    }
}
