package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class RecommendationService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public RecommendationService(UserStorage userStorage,
                                 FilmStorage filmStorage,
                                 FilmService filmService) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    /**
     * Получение рекомендаций для пользователя
     */
    public List<Film> getRecommendedFilms(long userId) {
        log.debug("Получение рекомендаций для пользователя ID: {}", userId);

        if (!userStorage.containsUserById(userId)) {
            throw new EntityNotFoundException("Пользователь с ID " + userId + " не найден", userId);
        }

        List<Long> similarUsers = userStorage.getUsersWithSimilarTastes(userId);
        log.debug("Найдено {} пользователей с похожими вкусами", similarUsers.size());

        if (similarUsers.isEmpty()) {
            log.debug("Не найдено пользователей с похожими вкусами");
            return new ArrayList<>();
        }

        Set<Film> recommendedFilms = new HashSet<>();
        for (Long similarUserId : similarUsers) {
            List<Film> filmsLikedBySimilarUser = filmStorage.getFilmsLikedByUser(similarUserId);
            recommendedFilms.addAll(filmsLikedBySimilarUser);
        }

        List<Film> filmsLikedByCurrentUser = filmStorage.getFilmsLikedByUser(userId);
        recommendedFilms.removeAll(filmsLikedByCurrentUser);

        log.debug("Найдено {} рекомендаций", recommendedFilms.size());

        List<Film> result = new ArrayList<>(recommendedFilms);
        result.forEach(filmService::addData);

        return result;
    }
}