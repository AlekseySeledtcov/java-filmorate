package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotations.Login;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Builder
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

    @Builder.Default
    private Set<Long> friendsList = new HashSet<>();

    public void setName(String name) {
        this.name = (name == null || name.trim().isEmpty()) ? this.login : name;
    }

    @JsonCreator
    public User(
            @JsonProperty("id") Long id,
            @JsonProperty("email") String email,
            @JsonProperty("login") String login,
            @JsonProperty("name") String name,
            @JsonProperty("birthday") LocalDate birthday,
            @JsonProperty("friendsList") Set<Long> friendsList) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.setName(name);
        this.birthday = birthday;
        this.friendsList = friendsList != null ? friendsList : new HashSet<>();
    }
}