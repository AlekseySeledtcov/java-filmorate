package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import lombok.*;
import ru.yandex.practicum.filmorate.annotations.Login;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class User {
    private Long id;
    @NotEmpty
    @Email
    private String email;
    @Login
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
    private Set<Long> friendsList;

    public User(String email, String login, String name, LocalDate birthday) {
        this.id = 0L;
        this.email = email;
        this.login = login;
        this.name = (name == null || name == "") ? login : name;
        this.birthday = birthday;
        this.friendsList = new HashSet<>();
    }
}