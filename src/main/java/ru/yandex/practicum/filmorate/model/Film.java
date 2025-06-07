package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.After1895;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    private Long id;
    @NotEmpty
    @NotBlank
    private String name;
    @Size(min = 0, max = 200)
    private String description;
    @After1895
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
}
