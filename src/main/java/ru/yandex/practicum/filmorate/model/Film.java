package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotations.After1895;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    private Long id;
    @NotEmpty
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @After1895
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private long likesCount;
    @Builder.Default
    private List<Genre> genres = new ArrayList<>();
    @Builder.Default
    private List<Director> directors = new ArrayList<>();
    private Mpa mpa;

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        id = 0L;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
