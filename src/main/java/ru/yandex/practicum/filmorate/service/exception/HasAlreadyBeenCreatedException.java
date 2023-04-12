package ru.yandex.practicum.filmorate.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class HasAlreadyBeenCreatedException extends RuntimeException {
    public HasAlreadyBeenCreatedException() {
        super("The object has already been created");
    }
}
