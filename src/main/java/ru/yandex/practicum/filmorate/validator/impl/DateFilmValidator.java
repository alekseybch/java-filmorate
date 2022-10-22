package ru.yandex.practicum.filmorate.validator.impl;

import ru.yandex.practicum.filmorate.validator.DateFilmValidate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateFilmValidator implements ConstraintValidator<DateFilmValidate, LocalDate> {

    private final static LocalDate FIRST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate releaseDate,
                           ConstraintValidatorContext cxt) {
        return releaseDate != null && releaseDate.isAfter(FIRST_RELEASE_DATE);
    }
}