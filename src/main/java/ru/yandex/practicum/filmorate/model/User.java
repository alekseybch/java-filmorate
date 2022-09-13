package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;
    @NotNull
    @Email(message = "Неверный формат.")
    private String email;
    @NotNull(message = "не может быть null.")
    @NotBlank(message = "не может быть пустым.")
    @Pattern(regexp = "[\\S]{0,}", message = "не должен содердать пробелов.")
    private String login;
    private String name;
    @NotNull(message = "не может быть null.")
    @Past(message = "не может быть позже текущего дня.")
    private LocalDate birthday;
}
