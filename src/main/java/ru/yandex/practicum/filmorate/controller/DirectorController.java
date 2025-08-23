package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public List<Director> getDirectors() {
        log.debug("DirectorController. Get запрос на получение списка режиссеров");
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable("id") long id) {
        log.debug("DirectorController. Get запрос на получение режисера по ID {}", id);
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Director postDirector(@Valid @RequestBody Director director) {
        log.debug("DirectorController. Post запрос на создание режиссера {}", director.getName());
        return directorService.postDirector(director);
    }

    @PutMapping
    public Director putDirector(@Valid @RequestBody Director director) {
        log.debug("DirectorController. Put запрос на изменение режисера NAME {}", director.getName());
        return directorService.putDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable("id") long id) {
        log.debug("DirectorController. Delete запрос на удаление режисера по ID {}", id);
        directorService.deleteDirector(id);
    }

}
