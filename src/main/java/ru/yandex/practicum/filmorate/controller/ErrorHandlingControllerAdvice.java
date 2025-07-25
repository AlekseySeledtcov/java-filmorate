package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.ValidationErrorResponse;
import ru.yandex.practicum.filmorate.model.Violation;

@Slf4j
@ControllerAdvice
public class ErrorHandlingControllerAdvice {
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onConstraintValidationException(final ConstraintViolationException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            error.getViolations().add(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.getViolations().add(new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return error;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    ErrorResponse notFoundUserByIdExceptionHandler(final NotFoundUserByIdException e) {
        log.warn("Исключение, пользователь с id {} не найден", e.getId());
        return new ErrorResponse("Пользователь не найден", e.getDetailMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    ErrorResponse notFoundUserByFriendIdException(final NotFoundUserByFriendIdException e) {
        log.warn("Исключение, пользователь с friendId {} не найден", e.getFriendId());
        return new ErrorResponse("Пользователь не найден", e.getDetailMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    ErrorResponse notFoundFriendshipException(final NotFoundFriendshipException e) {
        log.warn("Исключение, для пользователя с id {} не найден друг с friendId {}", e.getId(), e.getFriendId());
        return new ErrorResponse("Дружба не найдена", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    ErrorResponse notFoundFilmException(final NotFoundFilmException e) {
        log.warn("Исключение, фильм с Id {} не найден", e.getId());
        return new ErrorResponse("Фильм не найден", e.getDetailMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    ErrorResponse notFoundMpaException(final NotFoundMpaException e) {
        log.warn("Исключение, рейтин MPA по id {} не найден", e.getId());
        return new ErrorResponse("Рейтинг MPA не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    ErrorResponse notFoundGenreException(final NotFoundGenreException e) {
        log.warn("Исключение, жанр по id {} не найден", e.getMessage());
        return new ErrorResponse("Жанр не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse("Ошибка", "Произошла непредвиденная ошибка.");
    }
}
