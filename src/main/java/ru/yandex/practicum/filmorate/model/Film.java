package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.validator.DateFilmValidate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
@Builder
public class Film {
    int id;
    @NotNull(message = "Can't be null.")
    @NotBlank(message = "Can't be blank.")
    @Size(max = 150, message = "Must not be more than 150 characters.")
    private String name;
    @Size(max = 200, message = "Must not be more than 200 characters.")
    private String description;
    @DateFilmValidate
    private LocalDate releaseDate;
    @Positive(message = "Must be a positive number.")
    private int duration;
    private int rate;
    private Mpa mpa;
    private final Set<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
    @JsonIgnore
    private final Set<Integer> likes = new HashSet<>();

    public void addLike(Integer id) {
        likes.add(id);
        rate = likes.size(); // тут должен быть сложный расчет рейтинга ^^
    }

    public void deleteLike(Integer id) {
        if (!likes.remove(id)) {
            throw new NotFoundException(String.format("Like with id = %d not found", id));
        }
        rate = likes.size();
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void deleteAllGenres() {
        genres.clear();
    }
}