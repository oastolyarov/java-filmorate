package ru.yandex.practicum.exceptions;

public class FilmNotExistsException extends RuntimeException {
    public FilmNotExistsException(String message) {
        super(message);
    }
}
