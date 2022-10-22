package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
public class Genre {
    private int id;
    @Size(max = 20, message = "Must not be more than 20 characters.")
    private String name;
}