package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Recommendations {
    private Long userId;
    private List<Film> recommendedFilms;
}