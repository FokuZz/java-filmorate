package ru.yandex.practicum.filmorate.service.exception;

public class IdCoppyException extends RuntimeException {
    public IdCoppyException() {
        super("This id has used");
    }
}
