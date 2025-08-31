package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;


import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
public class EventServiceTest {
    private final UserDbStorage userDbStorage;
    private final UserService userService;
    private final EventService eventService;

    @Test
    public void testGetAllEventsByUserId() {
        User user1 = User.builder()
                .email("email@1gmail.com")
                .login("login1")
                .name("name 1")
                .birthday(LocalDate.now())
                .build();
        user1 = userDbStorage.addUser(user1);

        User user2 = User.builder()
                .email("email@2gmail.com")
                .login("login2")
                .name("name 2")
                .birthday(LocalDate.now())
                .build();
        user2 = userDbStorage.addUser(user2);

        userService.addToFriendsList(user1.getId(), user2.getId());

        Event event = eventService.getUserFeed(user1.getId()).getFirst();

        assertThat(event)
                .hasFieldOrPropertyWithValue("eventId", 1L)
                .hasFieldOrPropertyWithValue("userId", user1.getId())
                .hasFieldOrPropertyWithValue("entityId", event.getEntityId())
                .hasFieldOrPropertyWithValue("eventType", EventType.FRIEND)
                .hasFieldOrPropertyWithValue("operation", Operation.ADD);
    }
}