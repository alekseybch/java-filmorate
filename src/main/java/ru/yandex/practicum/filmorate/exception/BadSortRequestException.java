package ru.yandex.practicum.filmorate.exception;

public class BadSortRequestException extends RuntimeException{
    public BadSortRequestException(String s) {
        super(s);
    }
}
