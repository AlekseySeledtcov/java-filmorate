package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Recommendations;
import ru.yandex.practicum.filmorate.service.RecommendationService;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    /**
     * Получение рекомендаций для пользователя
     * POSTMAN ожидает: GET /users/{id}/recommendations
     */
    @GetMapping("/users/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable("id") long userId) {
        log.debug("GET /users/{}/recommendations - получение рекомендаций", userId);

        try {
            Recommendations recommendations = recommendationService.getRecommendations(userId);
            List<Film> recommendedFilms = recommendations.getRecommendedFilms();
            log.debug("Найдено {} рекомендаций для пользователя ID: {}", recommendedFilms.size(), userId);
            return recommendedFilms;
        } catch (Exception e) {
            log.error("Ошибка при получении рекомендаций для пользователя {}: {}", userId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}