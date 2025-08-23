package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DirectorControllerTest {

    @Autowired
    private DirectorController directorController;

    private Director director1;
    private Director director2;
    private Director director3;
    private Director director4;

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
    }

    @Test
    void getDirectorsTest() {
        List<Director> directors = directorController.getDirectors();

        assertEquals(directors.size(), 4, "Полученное значение " +
                "не соответствует колличеству добавленных режиссеров");
        assertEquals(directors.get(0).getName(), director1.getName());
        assertEquals(directors.get(1).getName(), director2.getName());
        assertEquals(directors.get(2).getName(), director3.getName());
        assertEquals(directors.get(3).getName(), director4.getName());
    }

    @Test
    void getDirectorByIdTest() {
        Director expected = directorController.getDirectorById(director3.getId());
        assertEquals(expected, director3, "Данные не совпадают");
    }

    @Test
    void putDirectorTest() {
        director3.setName("Стивен Спилберг");
        Director expected = directorController.putDirector(director3);
        assertEquals(expected, director3, "Данные не совпадают");
    }

    @Test
    void deleteDirectorTest() {
        directorController.deleteDirector(2);
        assertEquals(directorController.getDirectors().size(), 3);
    }
}
