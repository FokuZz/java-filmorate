package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The object has already been created")
public class HasAlreadyBeenCreatedException extends RuntimeException {
    public HasAlreadyBeenCreatedException() {
        super("The object has already been created");
    }
}
