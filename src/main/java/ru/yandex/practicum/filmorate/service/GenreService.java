package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DataStorage;

@Service
public class GenreService extends AbstractService<Genre>{
    public GenreService(@Qualifier("genreDbStorage") DataStorage<Genre> genreStorage) {
        super(genreStorage);
    }
}