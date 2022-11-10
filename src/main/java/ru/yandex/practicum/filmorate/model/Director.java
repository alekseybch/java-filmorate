package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class Director {
    private int id;
    @NotNull(message = "Can't be null.")
    @NotBlank(message = "Can't be blank.")
    @Size(max = 70, message = "Must not be more than 20 characters.")
    private String name;
}
