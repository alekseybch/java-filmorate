package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class User {
    private int id;
    @NotNull
    @Email(message = "Invalid email format.")
    private String email;
    @NotNull(message = "Can't be null.")
    @NotBlank(message = "Can't be blank.")
    @Pattern(regexp = "[\\S]{0,}", message = "Must not contain spaces.")
    private String login;
    private String name;
    @NotNull(message = "Can't be null.")
    @Past(message = "It can't be later than the current day.")
    private LocalDate birthday;
    private Set<Integer> friends;
}
