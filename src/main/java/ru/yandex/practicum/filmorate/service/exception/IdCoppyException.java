package ru.yandex.practicum.filmorate.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class IdCoppyException extends RuntimeException {
    public IdCoppyException() {
        super("This id has used");
    }
}
