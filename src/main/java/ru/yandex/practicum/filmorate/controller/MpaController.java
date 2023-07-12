package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final MpaDao mpaDao;

    @GetMapping
    public List<Mpa> listMpa() {
        return mpaDao.getRatings();
    }

    @GetMapping("/{id}")
    public Mpa findMpaById(@PathVariable int id) {
        return mpaDao.getRatingById(id);
    }
}
