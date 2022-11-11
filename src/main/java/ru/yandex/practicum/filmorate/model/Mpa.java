package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class Mpa {
    private Integer id;
    @NotBlank(message = "Can't be null or blank")
    @Size(max = 5, message = "Must not be more than 5 characters.")
    private String name;
    private String description;
}