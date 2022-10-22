package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
public class Mpa {
    private int id;
    @Size(max = 5, message = "Must not be more than 5 characters.")
    private String name;
    private String description;
}