package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Director {
    private long id;
    @NotEmpty
    @NotBlank
    private String name;
}
