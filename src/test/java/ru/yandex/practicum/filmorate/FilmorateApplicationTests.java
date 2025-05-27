package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {
    User user1;
    Film film1;
    UserValidator uv;
    FilmValidator fv;

    @BeforeEach
    public void beforeEach() {
        uv = new UserValidator();
        fv = new FilmValidator();
        user1 = User.builder()
                .name("Name")
                .email("Email@email")
                .login("Login")
                .birthday(LocalDate.of(1980, 6, 19))
                .build();

        film1 = Film.builder()
                .name("FilmName")
                .description("FilmDescription")
                .releaseDate(LocalDate.of(2021, 9, 16))
                .duration(155)
                .build();
    }

    @Test
    void checkingThatTheEmailIsIncorrect() {
        assertThrows(ValidationException.class, () -> {
            user1.setEmail("");
            uv.validate(user1);
        }, "Исключение, поле email не должно быть пустым");

        user1.setEmail("email");
        assertThrows(ValidationException.class, () -> {
            uv.validate(user1);
        }, "Исключение, поле email должно содержать символ @");
    }

    @Test
    void checkingThatTheLoginIsIncorrect() {
        user1.setEmail("emeail@email");
        user1.setLogin("");
        assertThrows(ValidationException.class, () -> {
            uv.validate(user1);
        }, "Исключение, поле login не должно быть пустым");

        user1.setEmail("login login");
        assertThrows(ValidationException.class, () -> {
            uv.validate(user1);
        }, "Исключение, поле login не должно содержать пробелы");
    }

    @Test
    void checkThatIfTheNameIsEmptyThenLoginIsUsedInsteadOfTheName() {
        user1.setName("");
        assertEquals(user1.getLogin(), uv.validate(user1).getName());
    }

    @Test
    void checkingThatTheBirthdayDateCannotBeInTheFuture() {
        user1.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> {
            uv.validate(user1);
        }, "Исключение, дата дня рождения не может быть в будующем");
    }

    @Test
    void checkingThatTheFilmNameIsIncorrect() {
        film1.setName("");
        assertThrows(ValidationException.class, () -> {
            fv.validate(film1);
        }, "Исключение, поля name не должно быть пустым");

        film1.setName(null);
        film1.setName("");
        assertThrows(ValidationException.class, () -> {
            fv.validate(film1);
        }, "Исключение, поля name не должно быть пустым");
    }

    @Test
    void checkThatTheDescriptionLengthCannotBeMoreThan200Characters() {
        film1.setDescription("Это было самое большое описание к фильму " +
                "которого нет и которое я не смог придумать, это было самое большое описание " +
                "к фильму которого нет и которое я не смог придумать, это было самое большое " +
                "описание к фильму которого нет и которое я не смог придумать");
        assertThrows(ValidationException.class, () -> {
            fv.validate(film1);
        }, "Исключение, максимальная длинна описания - 200 симвлов");
    }

    @Test
    void checkMovieReleaseDate() {
        film1.setReleaseDate(LocalDate.of(1800, 1, 1));
        assertThrows(ValidationException.class, () -> {
            fv.validate(film1);
        }, "Исключение, дата релиза фильма не должна быть раньше 28 декабря 1895 года");
    }

    @Test
    void movieDurationCheckMustBePositive() {
        film1.setDuration(-1);
        assertThrows(ValidationException.class, () -> {
            fv.validate(film1);
        }, "Исключение, продолжительность фильма должна быть положительной");
    }
}
