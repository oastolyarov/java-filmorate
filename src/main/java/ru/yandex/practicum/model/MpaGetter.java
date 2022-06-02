package ru.yandex.practicum.model;

import lombok.Data;

import java.util.List;
@Data
public class MpaGetter {
    private int id;
    private String name;

    public MpaGetter() {
        this.name = List.of(Mpa.values()).get(id).getName();
    }
}
