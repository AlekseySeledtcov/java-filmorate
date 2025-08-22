package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Qualifier("UserDbStorage")
@Repository
public class UserDbStorage extends BaseStorage<User> implements UserStorage {

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday) " +
            "VALUES(?, ?, ?, ?)";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET name = ?, login = ?, " +
            "email = ?, birthday = ?";
    private static final String CONTAINS_BY_ID_QUERY = " SELECT COUNT(*) FROM users WHERE id = ?";
    private static final String CONTAINS_BY_EMAIL_QUERY = " SELECT COUNT(*) FROM users WHERE email = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_ALL_FRIENDS_BY_USER_ID_QUERY =
            "SELECT u.* FROM users AS u " +
                    "JOIN friends_list AS fl ON u.id = fl.friend_id " +
                    "WHERE fl.user_id = ? AND fl.status = 'CONFIRMED'";

    private static final String FIND_COMMON_FRIENDS_BY_USER_ID_QUERY =
            "SELECT u.* FROM users AS u " +
                    "JOIN friends_list AS f1 ON u.id = f1.friend_id AND f1.status = 'CONFIRMED' " +
                    "JOIN friends_list AS f2 ON u.id = f2.friend_id AND f2.status = 'CONFIRMED' " +
                    "WHERE f1.user_id = ? AND f2.user_id = ?";

    @Override
    public User addUser(User user) {
        log.debug("Хранилище. addUser");
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday())
        );
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.debug("Хранилище. updateUser Обновление полей пользователя name {}", user.getName());
        update(
                UPDATE_QUERY,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday()
        );
        return user;
    }

    @Override
    public List<User> getUsersList() {
        log.debug("Получение списка пользователей из ДБ");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> getUser(long id) {
        log.debug("Получение пользователя из храниоища по id {}", id);
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public boolean containsUserById(long id) {
        log.debug("Хранилище. containsUser Проверка ниличия пользователя в БД с id {}", id);
        long count = jdbc.queryForObject(CONTAINS_BY_ID_QUERY, long.class, id);
        return count > 0;
    }

    @Override
    public boolean containsUserByEmail(String email) {
        log.debug("Хранилище. containsUser Проверка ниличия пользователя в БД с email {}", email);
        long count = jdbc.queryForObject(CONTAINS_BY_EMAIL_QUERY, long.class, email);
        return count > 0;
    }

    @Override
    public List<User> getUserFriendsList(long id) {
        log.debug("Хранилище. getUserFriendsList Получение списка друзей пользователя с id {}", id);
        return findMany(FIND_ALL_FRIENDS_BY_USER_ID_QUERY, id).stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getUsersCommonFriendList(Long id, Long otherId) {
        log.debug("Хранилище. getUsersCommonFriendList Получение списка общих друзей");
        return findMany(FIND_COMMON_FRIENDS_BY_USER_ID_QUERY, id, otherId);
    }

    @Override
    public List<Long> getUsersWithSimilarTastes(long userId) {
        log.debug("Поиск пользователей с похожими вкусами для пользователя ID: {}", userId);
        String sql =
                "SELECT DISTINCT l1.user_id, COUNT(l1.film_id) as film_count " +
                        "FROM like_list l1 " +
                        "JOIN like_list l2 ON l1.film_id = l2.film_id " +
                        "WHERE l2.user_id = ? AND l1.user_id != ? " +
                        "GROUP BY l1.user_id " +
                        "ORDER BY film_count DESC " +
                        "LIMIT 10";

        return jdbc.query(sql,
                (rs, rowNum) -> rs.getLong("user_id"),
                userId, userId
        );
    }

    @Override
    public List<User> getUsersByIds(List<Long> userIds) {
        log.debug("Хранилище. getUsersByIds получение пользователей по списку ID: {}", userIds);

        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }

        String placeholders = userIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String query = String.format("SELECT * FROM users WHERE id IN (%s)", placeholders);

        return findMany(query, userIds.toArray());
    }
}