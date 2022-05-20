package ru.yandex.practicum.exceptions;

public class FilmIdValidationException extends RuntimeException {
    public FilmIdValidationException(String message) {
        super(message);
    }
}
