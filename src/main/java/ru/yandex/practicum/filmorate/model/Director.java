package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Director {
    private long id;
    @NotEmpty
    @NotBlank
    private String name;

    public Director(String name) {
        this.name = name;
    }
}
