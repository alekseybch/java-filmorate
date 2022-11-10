package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DataStorage;

@Service
public class DirectorService extends AbstractService<Director> {
    public DirectorService(@Qualifier("directorDbStorage") DataStorage<Director> directorStorage) {
        super(directorStorage);
    }
}
