package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.annotations.After1895;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private List<Genre> genre;
    private List<Rating> rating;

    public Film(String name, String description, LocalDate releaseDate, Integer duration,
                List<Genre> genre, List<Rating> rating) {
        id = 0L;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        likeList = new HashSet<>();
        genre = new ArrayList<>();
        this.genre.addAll(genre);
        rating = new ArrayList<>();
        this.rating.addAll(rating);
    }

    public void updateFilmLikeList(long userId) {
        likeList.add(userId);
    }
}
