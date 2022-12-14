package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.validator.DateFilmValidate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private Integer id;
    @NotBlank(message = "Can't be null or blank.")
    @Size(max = 150, message = "Must not be more than 150 characters.")
    private String name;
    @Size(max = 200, message = "Must not be more than 200 characters.")
    private String description;
    @DateFilmValidate
    private LocalDate releaseDate;
    @Positive(message = "Must be a positive number.")
    private Integer duration;
    private int rate;
    private Mpa mpa;
    private final LinkedHashSet<Genre> genres = new LinkedHashSet<>();
    private final Set<Director> directors = new HashSet<>();
    @JsonIgnore
    private final Set<Integer> likes = new HashSet<>();

    public void addLike(Integer id) {
        likes.add(id);
        rate = likes.size(); // тут должен быть сложный расчет рейтинга ^^
    }

    public void deleteLike(Integer id) {
        if (!likes.remove(id)) {
            throw new NotFoundException(String.format("Like with user id = %d not found", id));
        }
        rate = likes.size();
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void addDirector(Director director) {
        directors.add(director);
    }
}