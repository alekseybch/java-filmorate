package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class Review {
    private Integer reviewId;
    @NotBlank(message = "Can't be null or blank.")
    @Size(max = 200, message = "Must not be more than 200 characters.")
    private String content;
    @NotNull(message = "Can't be null.")
    @JsonProperty("isPositive")
    private Boolean isPositive;
    @NotNull(message = "Can't be null.")
    private Integer userId;
    @NotNull(message = "Can't be null.")
    private Integer filmId;
    private Integer useful;
}