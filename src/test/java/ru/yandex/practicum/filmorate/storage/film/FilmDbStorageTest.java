package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class})
class FilmDbStorageTest {
    private final FilmService filmService;
    private final FilmDbStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserService userService;
    private final DirectorService directorService;

    private Film newFilm1;
    private Film newFilm2;
    private User newUser1;
    private User newUser2;

    @BeforeEach
    void setUp() {
        newFilm1 = Film.builder()
                .name("New Film1")
                .description("New film1 fot test description")
                .releaseDate(LocalDate.of(2025, 07, 22))
                .duration(90)
                .genres(List.of(new Genre(1, "Комедия")))
                .directors(List.of(new Director(1, "Квентин Тарантино")))
                .mpa(new Mpa(1, "G"))
                .build();

        newFilm2 = Film.builder()
                .name("New Film2")
                .description("New film2 fot test description")
                .releaseDate(LocalDate.of(2025, 07, 23))
                .duration(100)
                .genres(List.of(new Genre(2, "Драма")))
                .directors(List.of(new Director(1, "Квентин Тарантино")))
                .mpa(new Mpa(5, "NC-17"))
                .build();

        newUser1 =  new User(
                "Theodore Deckow",
                "Quentin_Gislason@gmail.com",
                "Quentin_Gislason@gmail.com",
                LocalDate.of(1979, 03, 14)
        );

        newUser2 = new User(
                "Jeannie Graham",
                "Justyn44@gmail.com",
                "4UFuPkNVbG",
                LocalDate.of(2004, 06, 9)
        );
        directorService.postDirector(new Director("Квентин Тарантино"));
    }

    @Test
    void addFilm() {
        Film actualFilm = newFilm1;
        Film expectedFilm = filmService.addFilm(newFilm1);
        assertEquals(expectedFilm.getName(), actualFilm.getName());
        assertEquals(expectedFilm.getDescription(), actualFilm.getDescription());
        assertEquals(expectedFilm.getReleaseDate(), actualFilm.getReleaseDate());
        assertEquals(expectedFilm.getDuration(), actualFilm.getDuration());
        assertEquals(expectedFilm.getGenres(), actualFilm.getGenres());
        assertEquals(expectedFilm.getMpa(), actualFilm.getMpa());
    }

    @Test
    void updateFilm() {
        long id = filmStorage.addFilm(newFilm1).getId();
        Film actualFilm = newFilm2;
        newFilm2.setId(id);
        Film expectedFilm = filmStorage.updateFilm(newFilm2);
        assertEquals(expectedFilm, actualFilm);
    }

    @Test
    void getPopularFilmList() {
        newFilm1 = filmService.addFilm(newFilm1);
        newFilm2 = filmService.addFilm(newFilm2);
        userService.addUser(newUser1);
        userService.addUser(newUser2);
        likeStorage.putLike(1, newFilm1.getId());
        likeStorage.putLike(1, newFilm2.getId());
        likeStorage.putLike(2, newFilm2.getId());
    }

    @Test
    void getFilm() {
        Film actualFilm = newFilm1;
        Film expectedFilm = filmService.addFilm(actualFilm);
        assertEquals(expectedFilm, actualFilm);
    }

    @Test
    void containsFilmById() {
        Film film = filmService.addFilm(newFilm1);
        filmService.addFilm(newFilm2);
        assertTrue(filmStorage.containsFilmById(film.getId()));
        assertTrue(filmStorage.containsFilmByName(film.getName()));

    }
}