package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DataStorage;

@Service
public class MpaService extends AbstractService<Mpa>{
    public MpaService(@Qualifier("mpaDbStorage") DataStorage<Mpa> mpaStorage) {
        super(mpaStorage);
    }
}