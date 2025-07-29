package ru.yandex.practicum.filmorate.model;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Genre {
    private Integer id;
    private String name;
}