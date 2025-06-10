package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

@Component
public class LikeListComparator implements Comparator<Film> {
    @Override
    public int compare(Film o1, Film o2) {
        return o1.getLikeLIst().size() - o2.getLikeLIst().size();
    }
}
