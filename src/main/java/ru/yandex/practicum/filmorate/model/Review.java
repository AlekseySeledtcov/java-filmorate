package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Модель отзыва на фильм.
 * Содержит информацию о содержании отзыва, его тональности, авторе, фильме и рейтинге полезности.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    private Long reviewId;

    @NotBlank(message = "Содержание отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Тип отзыва должен быть указан")
    private Boolean isPositive;

    @NotNull(message = "ID пользователя должен быть указан")
    private Long userId;

    @NotNull(message = "ID фильма должен быть указан")
    private Long filmId;

    @Builder.Default
    private Integer useful = 0;

    private LocalDateTime created;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    @Builder.Default
    private Set<Long> dislikes = new HashSet<>();
}