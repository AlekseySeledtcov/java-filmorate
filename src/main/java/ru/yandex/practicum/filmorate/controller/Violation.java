package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Violation {

    @Getter
    private final String fieldName;
    @Getter
    private final String message;

    public Violation(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
        log.warn("Поле {} - {}", fieldName, message);
    }
}
