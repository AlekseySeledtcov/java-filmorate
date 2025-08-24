package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;
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
    public ValidationErrorResponse onConstraintValidationException(final ConstraintViolationException exception) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (ConstraintViolation violation : exception.getConstraintViolations()) {
            error.getViolations().add(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            error.getViolations().add(new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return error;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse notFoundUserByIdExceptionHandler(final NotFoundUserByIdException exception) {
        log.warn("Исключение, пользователь с id {} не найден", exception.getId());
        return new ErrorResponse("Пользователь не найден", exception.getDetailMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse notFoundUserByFriendIdException(final NotFoundUserByFriendIdException exception) {
        log.warn("Исключение, пользователь с friendId {} не найден", exception.getFriendId());
        return new ErrorResponse("Пользователь не найден", exception.getDetailMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ErrorResponse notFoundFriendshipException(final NotFoundFriendshipException exception) {
        log.warn("Исключение, для пользователя с id {} не найден друг с friendId {}", exception.getId(), exception.getFriendId());
        return new ErrorResponse("Дружба не найдена", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse notFoundFilmException(final NotFoundFilmException exception) {
        log.warn("Исключение, фильм с Id {} не найден", exception.getId());
        return new ErrorResponse("Фильм не найден", exception.getDetailMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse notFoundMpaException(final NotFoundMpaException exception) {
        log.warn("Исключение, рейтинг MPA по id {} не найден", exception.getId());
        return new ErrorResponse("Рейтинг MPA не найден", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse notFoundGenreException(final NotFoundGenreException exception) {
        log.warn("Исключение, жанр по id {} не найден", exception.getMessage());
        return new ErrorResponse("Жанр не найден", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse notFoundEntityByIdException(final NotFoundEntityByIdException exception) {
        log.warn("Исключение, {}", exception.getDetailMessage());
        return new ErrorResponse(exception.getMessage(), exception.getDetailMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleThrowable(final Throwable exception) {
        return new ErrorResponse("Ошибка", "Произошла непредвиденная ошибка.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse notFoundReviewException(final NotFoundReviewException exception) {
        log.warn("Исключение, отзыв с Id {} не найден", exception.getId());
        return new ErrorResponse("Отзыв не найден", exception.getDetailMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse notFoundReactionException(final NotFoundReactionException exception) {
        log.warn("Исключение, оценка отзыва не найдена - reviewId: {}, userId: {}",
                exception.getReviewId(), exception.getUserId());
        return new ErrorResponse("Оценка отзыва не найдена", exception.getDetailMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse validationException(final ValidationException exception) {
        log.warn("Ошибка валидации: {}", exception.getMessage());
        return new ErrorResponse("Ошибка валидации", exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleAllExceptions(Exception ex) {
        log.error("Необработанное исключение: ", ex);
        return new ErrorResponse("Внутренняя ошибка сервера", "Произошла непредвиденная ошибка");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNoResourceFoundException(NoResourceFoundException ex) {
        log.warn("Ресурс не найден: {}", ex.getMessage());
        return new ErrorResponse("Ресурс не найден", "Запрашиваемый ресурс не существует");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMissingParams(MissingServletRequestParameterException ex) {
        log.warn("Отсутствует обязательный параметр: {}", ex.getParameterName());
        return new ErrorResponse("Ошибка запроса", "Отсутствует обязательный параметр: " + ex.getParameterName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Ошибка чтения тела запроса: {}", ex.getMessage());
        return new ErrorResponse("Тело запроса не может быть пустым",
                "Проверьте формат JSON и наличие обязательных полей");
    }


}
