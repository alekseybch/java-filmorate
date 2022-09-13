package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = DateFilmValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DateFilmValidate {
    String message() default "неверная дата, не раньше 28 декабря 1895 года.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
