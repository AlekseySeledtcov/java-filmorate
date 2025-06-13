package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResponse {

    @Getter
    @Setter
    private List<Violation> violations = new ArrayList<>();
}
