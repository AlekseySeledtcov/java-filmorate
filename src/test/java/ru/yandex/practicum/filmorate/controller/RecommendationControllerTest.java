package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RecommendationControllerTest {

    @Autowired
    private RecommendationController recommendationController;

    @Autowired
    private UserService userService;

    @Autowired
    private FilmService filmService;

    private User user1;
    private User user2;
    private Film film1;
    private Film film2;

    @BeforeEach
    void setUp() {
        user1 = new User("user1@mail.ru", "user1", "User One", LocalDate.of(1990, 1, 1));
        user2 = new User("user2@mail.ru", "user2", "User Two", LocalDate.of(1990, 1, 1));

        user1 = userService.addUser(user1);
        user2 = userService.addUser(user2);

        film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        film1.setMpa(new Mpa(1, "G"));

        film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2000, 1, 1));
        film2.setDuration(120);
        film2.setMpa(new Mpa(1, "G"));

        film1 = filmService.addFilm(film1);
        film2 = filmService.addFilm(film2);
    }

    @Test
    void testGetRecommendationsEndpoint() {
        filmService.putLikeToFilm(film1.getId(), user1.getId());

        filmService.putLikeToFilm(film1.getId(), user2.getId());
        filmService.putLikeToFilm(film2.getId(), user2.getId());

        List<Film> recommendations = recommendationController.getRecommendations(user1.getId());

        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
        assertEquals(film2.getId(), recommendations.get(0).getId());
    }

    @Test
    void testGetRecommendationsEndpointUserNotFound() {
        List<Film> recommendations = recommendationController.getRecommendations(999L);

        assertNotNull(recommendations);
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void testGetRecommendationsNoSimilarTastes() {
        filmService.putLikeToFilm(film1.getId(), user1.getId());

        filmService.putLikeToFilm(film2.getId(), user2.getId());

        List<Film> recommendations = recommendationController.getRecommendations(user1.getId());

        assertNotNull(recommendations);
        assertTrue(recommendations.isEmpty());
    }
}