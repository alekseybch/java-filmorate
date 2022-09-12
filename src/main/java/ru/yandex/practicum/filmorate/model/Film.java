package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.DateFilmValidate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id;
    @NotNull(message = "не может быть null.")
    @NotBlank(message = "не может быть пустым.")
    private String name;
    @Size(max = 200, message = "не должен быть больше 200 символов.")
    private String description;
    @DateFilmValidate
    private LocalDate releaseDate;
    @Positive(message = "должен быть положительным числом.")
    private int duration;
}
