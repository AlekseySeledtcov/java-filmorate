package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundMpaException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Slf4j
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public Mpa getMpaById(long id) {
        if (id > getAllMpa().size()) {
            log.warn("MpaDbStorage. getMpaById. Жанр Mpa по id {} отсутствует в базе", id);
            throw new NotFoundMpaException(String.format("Жанр Mpa по id %d отсутствует в базе", id), id);
        }
        return mpaStorage.getMpaById(id);
    }
}


