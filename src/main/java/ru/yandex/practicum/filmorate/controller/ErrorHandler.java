package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.service.exception.HasAlreadyBeenCreatedException;
import ru.yandex.practicum.filmorate.service.exception.HasNoBeenFoundException;
import ru.yandex.practicum.filmorate.service.exception.IdCoppyException;

import javax.validation.ValidationException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({HasAlreadyBeenCreatedException.class, IdCoppyException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse errorValid(final RuntimeException e){
        return new ErrorResponse("Validation error occurred.", e.getMessage());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse errorNotFound(final HasNoBeenFoundException e){
        return new ErrorResponse("An error occurred not found.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationBootError(final ValidationException e){
        return new ErrorResponse("Validation error occurred.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationBootError(final MethodArgumentNotValidException e){
        return new ErrorResponse("Validation error occurred.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse errorUnexpected(final Throwable e){
        return new ErrorResponse("An unexpected error has occurred.", e.toString());
    }

    @AllArgsConstructor
    @Getter
    static class ErrorResponse {
        private String error;

        private String errorMessage;
    }
}
