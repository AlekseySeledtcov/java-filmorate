package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotations.After1895;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    private Long id;

    @NotEmpty
    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @After1895
    private LocalDate releaseDate;

    @Positive
    private Integer duration;

    @Builder.Default
    private long likesCount = 0;

    @Builder.Default
    private List<Genre> genres = new ArrayList<>();

    @Builder.Default
    private List<Director> directors = new ArrayList<>();

    private Mpa mpa;

    public void setDescription(String description) {
        this.description = (description != null) ? description : "";
    }

    @JsonCreator
    public static Film fromJson(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("releaseDate") LocalDate releaseDate,
            @JsonProperty("duration") Integer duration,
            @JsonProperty("mpa") Mpa mpa,
            @JsonProperty("genres") List<Genre> genres,
            @JsonProperty("directors") List<Director> directors,
            @JsonProperty("likesCount") Long likesCount) {
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(mpa)
                .genres(genres != null ? genres : new ArrayList<>())
                .directors(directors != null ? directors : new ArrayList<>())
                .likesCount(likesCount != null ? likesCount : 0)
                .build();
    }
}