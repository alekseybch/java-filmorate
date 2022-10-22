package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.validator.impl.DateFilmValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = DateFilmValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DateFilmValidate {
    String message() default "Invalid date. First release date 1895-12-28.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}