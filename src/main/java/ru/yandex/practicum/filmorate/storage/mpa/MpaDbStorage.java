package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Slf4j
@Repository
public class MpaDbStorage extends BaseStorage<Mpa> implements MpaStorage {
    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper, Mpa.class);
    }

    private static final String GET_ALL_QUERY = "SELECT * FROM mpa_rating";
    private static final String GET_MPA_BY_ID_QUERY = "SELECT * FROM mpa_rating WHERE mpa_id=?";

    @Override
    public List<Mpa> getAllMpa() {
        log.debug("getAllMpa");
        return findMany(GET_ALL_QUERY);
    }

    @Override
    public Mpa getMpaById(long id) {
        log.debug("getMpaById id {}", id);
        return findOne(GET_MPA_BY_ID_QUERY, id).get();
    }
}
