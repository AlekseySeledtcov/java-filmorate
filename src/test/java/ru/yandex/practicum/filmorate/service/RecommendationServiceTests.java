package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RecommendationServiceTests {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserService userService;

    @Autowired
    private FilmService filmService;

    private User user1;
    private User user2;
    private User user3;
    private Film film1;
    private Film film2;
    private Film film3;
    private Film film4;

    @BeforeEach
    void setUp() {
        // Создаем пользователей
        user1 = new User("user1@mail.ru", "user1", "User One", LocalDate.of(1990, 1, 1));
        user2 = new User("user2@mail.ru", "user2", "User Two", LocalDate.of(1990, 1, 1));
        user3 = new User("user3@mail.ru", "user3", "User Three", LocalDate.of(1990, 1, 1));

        user1 = userService.addUser(user1);
        user2 = userService.addUser(user2);
        user3 = userService.addUser(user3);

        // Создаем фильмы
        film1 = new Film("Film 1", "Description 1", LocalDate.of(2000, 1, 1), 120);
        film1.setMpa(new Mpa(1, "G"));

        film2 = new Film("Film 2", "Description 2", LocalDate.of(2000, 1, 1), 120);
        film2.setMpa(new Mpa(1, "G"));

        film3 = new Film("Film 3", "Description 3", LocalDate.of(2000, 1, 1), 120);
        film3.setMpa(new Mpa(1, "G"));

        film4 = new Film("Film 4", "Description 4", LocalDate.of(2000, 1, 1), 120);
        film4.setMpa(new Mpa(1, "G"));

        film1 = filmService.addFilm(film1);
        film2 = filmService.addFilm(film2);
        film3 = filmService.addFilm(film3);
        film4 = filmService.addFilm(film4);
    }

    @Test
    void testGetRecommendationsWithSimilarTastes() {
        // User1 лайкает Film1 и Film2
        filmService.putLikeToFilm(film1.getId(), user1.getId());
        filmService.putLikeToFilm(film2.getId(), user1.getId());

        // User2 лайкает Film1, Film2 и Film3 (совпадает с User1 по Film1 и Film2)
        filmService.putLikeToFilm(film1.getId(), user2.getId());
        filmService.putLikeToFilm(film2.getId(), user2.getId());
        filmService.putLikeToFilm(film3.getId(), user2.getId());

        // User3 лайкает Film4 (нет совпадений с User1)
        filmService.putLikeToFilm(film4.getId(), user3.getId());

        // User1 должен получить рекомендацию Film3 (лайкнутый User2, но не User1)
        var recommendations = recommendationService.getRecommendations(user1.getId());

        assertNotNull(recommendations);
        assertEquals(1, recommendations.getRecommendedFilms().size());
        assertEquals(film3.getId(), recommendations.getRecommendedFilms().get(0).getId());
    }

    @Test
    void testGetRecommendationsNoSimilarUsers() {
        // User1 лайкает Film1
        filmService.putLikeToFilm(film1.getId(), user1.getId());

        // User2 лайкает Film4 (нет совпадений с User1)
        filmService.putLikeToFilm(film4.getId(), user2.getId());

        // User1 не должен получить рекомендаций
        var recommendations = recommendationService.getRecommendations(user1.getId());

        assertNotNull(recommendations);
        assertTrue(recommendations.getRecommendedFilms().isEmpty());
    }

    @Test
    void testGetRecommendationsUserNotExists() {
        // Попытка получить рекомендации для несуществующего пользователя
        assertThrows(ru.yandex.practicum.filmorate.exceptions.NotFoundUserByIdException.class,
                () -> recommendationService.getRecommendations(999L));
    }

    @Test
    void testGetRecommendationsMultipleSimilarUsers() {
        // User1 лайкает Film1
        filmService.putLikeToFilm(film1.getId(), user1.getId());

        // User2 лайкает Film1 и Film2
        filmService.putLikeToFilm(film1.getId(), user2.getId());
        filmService.putLikeToFilm(film2.getId(), user2.getId());

        // User3 лайкает Film1 и Film3
        filmService.putLikeToFilm(film1.getId(), user3.getId());
        filmService.putLikeToFilm(film3.getId(), user3.getId());

        // User1 должен получить Film2 и Film3
        var recommendations = recommendationService.getRecommendations(user1.getId());

        assertNotNull(recommendations);
        assertEquals(2, recommendations.getRecommendedFilms().size());

        List<Long> recommendedIds = recommendations.getRecommendedFilms().stream()
                .map(Film::getId)
                .toList();

        assertTrue(recommendedIds.contains(film2.getId()));
        assertTrue(recommendedIds.contains(film3.getId()));
    }

    @Test
    void testGetRecommendationsExcludeAlreadyLiked() {
        // User1 лайкает Film1
        filmService.putLikeToFilm(film1.getId(), user1.getId());

        // User2 лайкает Film1 и Film2
        filmService.putLikeToFilm(film1.getId(), user2.getId());
        filmService.putLikeToFilm(film2.getId(), user2.getId());

        // User1 тоже лайкает Film2
        filmService.putLikeToFilm(film2.getId(), user1.getId());

        // User1 не должен получить Film2 в рекомендациях (уже лайкнул)
        var recommendations = recommendationService.getRecommendations(user1.getId());

        assertNotNull(recommendations);
        assertTrue(recommendations.getRecommendedFilms().isEmpty());
    }
}