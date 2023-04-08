package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "This id has used")
public class IdCoppyException extends RuntimeException {
    public IdCoppyException() {
        super("This id has used");
    }
}
