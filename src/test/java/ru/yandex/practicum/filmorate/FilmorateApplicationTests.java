package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmorateApplicationTests {

    private Validator validator;

    private User user;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        user = User.builder()
                .email("Email@email.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1980, 6, 19))
                .build();

        film = Film.builder()
                .name("FilmName")
                .description("FilmDescription")
                .releaseDate(LocalDate.of(2021, 9, 16))
                .duration(155)
                .build();
    }

    @Test
    void checkingThatTheEmailIsIncorrect() {
        user.setEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void checkingThatTheLoginIsIncorrect() {
        user.setLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Логин не может быть пустым");
        violations.clear();
        user.setLogin(" ");
        violations = validator.validate(user);
        assertEquals(1, violations.size(), "Логин не может содержать символы пробела");
    }

    @Test
    void checkThatIfTheNameIsEmptyThenLoginIsUsedInsteadOfTheName() {
        User user1 = User.builder()
                .email("Email@email.ru")
                .login("Login")
                .name(null) // явно передаем null
                .birthday(LocalDate.of(1980, 6, 19))
                .build();

        assertEquals(user1.getLogin(), user1.getName(), "Если поле name пустое, то ему должно " +
                "присвоиться значение поля login");
    }

    @Test
    void checkingThatTheBirthdayDateCannotBeInTheFuture() {
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "дата дня рождения не может быть в будущем");
    }

    @Test
    void checkingThatTheFilmNameIsIncorrect() {
        film.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Название фильма не может быть пустым");

        violations.clear();

        film.setName(null);
        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Название фильма не может быть пустым");
    }

    @Test
    void checkThatTheDescriptionLengthCannotBeMoreThan200Characters() {
        StringBuilder description = new StringBuilder("Это было самое большое описание к фильму ");
        while (description.length() < 200) {
            description.append(description);
        }
        film.setDescription(description.toString());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Максимальная длина описания - 200 символов");
    }

    @Test
    void checkMovieReleaseDate() {
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(),
                "Дата релиза фильма не должна быть раньше 28 декабря 1895 года");
    }

    @Test
    void movieDurationCheckMustBePositive() {
        film.setDuration(-1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "продолжительность фильма должна быть положительной");
    }
}
