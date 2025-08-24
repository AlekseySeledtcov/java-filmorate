package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundEntityByIdException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DirectorServiceTest {

    @Autowired
    private DirectorController directorController;

    @Autowired
    private DirectorService directorService;

    @Autowired
    private DirectorDbStorage directorDbStorage;

    @Autowired
    private FilmDbStorage filmDbStorage;

    private Director director1;
    private Director director2;
    private Director director3;
    private Director director4;

    private Film film1;

    @BeforeEach
    void setUp() {
        director1 = new Director("Эльдар Рязанов");
        director2 = new Director("Леонид Гайдай");
        director3 = new Director("Сергей Бондарчук");
        director4 = new Director("Андрей Тарковский");

        director1 = directorController.postDirector(director1);
        director2 = directorController.postDirector(director2);
        director3 = directorController.postDirector(director3);
        director4 = directorController.postDirector(director4);

        film1 = Film.builder()
                .name("Служебный роман")
                .description("Анатолий Ефремович Новосельцев, рядовой служащий одного статистического управления, — " +
                        "человек робкий и застенчивый")
                .releaseDate(LocalDate.of(1977, 10, 26))
                .duration(145)
                .genres(List.of(new Genre(1, "Комедия")))
                .mpa(new Mpa(1, "G"))
                .build();

    }

    @Test
    void getDirectorByIdIfNotExistTest() {
        assertThrows(NotFoundEntityByIdException.class, () -> directorController.getDirectorById(15L));
    }

    @Test
    void putDirectorIfNotExistTest() {
        director1.setId(15L);
        assertThrows(NotFoundEntityByIdException.class, () -> directorController.putDirector(director1));
    }

    @Test
    void deleteDirectorIfNotExistTest() {
        assertThrows(NotFoundEntityByIdException.class, () -> directorController.deleteDirector(15L));
    }

    @Test
    void putDirectorsToFilmTest() {
        film1 = filmDbStorage.addFilm(film1);
        List<Director> directors = List.of(director1, director2, director3);
        directorService.putDirectorsToFilm(directors, 1L);
        assertEquals(directorService.getDirectorsByFilmId(film1.getId()).size(), 3);
    }

    @Test
    void getDirectorsByFilmIdTest() {
        film1 = filmDbStorage.addFilm(film1);
        List<Director> directors = List.of(director1, director2, director3);
        directorService.putDirectorsToFilm(directors, film1.getId());
        directorService.deleteDirectorsFromFilm(film1.getId());
        assertEquals(directorService.getDirectorsByFilmId(film1.getId()).size(), 0);
    }
}
