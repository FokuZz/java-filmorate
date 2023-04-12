package ru.yandex.practicum.filmorate.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class HasNoBeenFoundException extends RuntimeException{
    public HasNoBeenFoundException(){
        super("The object has no been Found");
    }
}
