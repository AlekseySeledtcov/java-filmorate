package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.annotations.After1895;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
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
    private Set<Long> likeList;

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        id = 0L;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        likeList = new HashSet<>();
    }

    public void updateFilmLikeList(long userId) {
        likeList.add(userId);
    }
}
