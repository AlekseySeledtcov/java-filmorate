package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.Login;

import java.time.LocalDate;

@Data
@Builder
public class User {

    private Long id;
    @NotEmpty
    @Email
    private String email;
    @Login
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;

    public User(Long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name == null ? login : name;
        this.birthday = birthday;
    }
}
