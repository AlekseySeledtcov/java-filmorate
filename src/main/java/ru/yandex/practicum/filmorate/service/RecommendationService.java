package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundUserByIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Recommendations;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class RecommendationService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public RecommendationService(@Qualifier("userDbStorage") UserStorage userStorage,
                                 @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    /**
     * Получение рекомендаций для пользователя
     */
    public Recommendations getRecommendations(long userId) {
        log.debug("Получение рекомендаций для пользователя ID: {}", userId);

        if (!userStorage.containsUserById(userId)) {
            log.warn("Пользователь с ID {} не найден", userId);
            throw new NotFoundUserByIdException("Пользователь с ID " + userId + " не найден", userId);
        }

        log.debug("Поиск пользователей с похожими вкусами...");
        List<Long> similarUsers = userStorage.getUsersWithSimilarTastes(userId);
        log.debug("Найдено {} пользователей с похожими вкусами", similarUsers.size());

        if (similarUsers.isEmpty()) {
            log.debug("Не найдено пользователей с похожими вкусами для пользователя ID: {}", userId);
            return new Recommendations(userId, Collections.emptyList());
        }

        Set<Film> recommendedFilms = new HashSet<>();
        for (Long similarUserId : similarUsers) {
            log.debug("Получение фильмов для пользователя ID: {}", similarUserId);
            List<Film> filmsLikedBySimilarUser = filmStorage.getFilmsLikedByUser(similarUserId);
            recommendedFilms.addAll(filmsLikedBySimilarUser);
        }

        log.debug("Получение фильмов текущего пользователя ID: {}", userId);
        List<Film> filmsLikedByCurrentUser = filmStorage.getFilmsLikedByUser(userId);
        recommendedFilms.removeAll(filmsLikedByCurrentUser);

        log.debug("Найдено {} рекомендаций для пользователя ID: {}", recommendedFilms.size(), userId);

        return new Recommendations(userId, new ArrayList<>(recommendedFilms));
    }
}
