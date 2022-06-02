package ru.yandex.practicum.model;

public enum Mpa {
    G(0, "У фильма нет возрастных ограничений"),
    PG(1, "Детям рекомендуется смотреть фильм с родителями"),
    PG13(2, "Детям до 13 лет просмотр не желателен"),
    R(3, "Лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
    NC17(4, "Лицам до 18 лет просмотр запрещён");
    private int id;
    private String name;

    Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
