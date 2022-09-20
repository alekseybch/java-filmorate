package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.DateFilmValidate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class Film {
    private int id;
    @NotNull(message = "Can't be null.")
    @NotBlank(message = "Can't be blank.")
    private String name;
    @Size(max = 200, message = "Must not be more than 200 characters.")
    private String description;
    @DateFilmValidate
    private LocalDate releaseDate;
    @Positive(message = "Must be a positive number.")
    private int duration;
    private Set<Integer> likes;
}
