package ru.yandex.practicum.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exceptions.FilmIdValidationException;
import ru.yandex.practicum.exceptions.FilmNotExistsException;
import ru.yandex.practicum.exceptions.UserIdNotValidException;
import ru.yandex.practicum.exceptions.ValidationException;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(UserIdNotValidException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserIdIsNotValid (final UserIdNotValidException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> userNotValid (final MethodArgumentNotValidException e) {
        return Map.of("error", "Данные пользователя не валидны. Проверьте дату рождения и корректность email.");
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> filmNotValid(final ValidationException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(FilmNotExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> filmNotFound(final FilmNotExistsException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(FilmIdValidationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> filmNotValid(final FilmIdValidationException e) {
        return Map.of("error", e.getMessage());
    }
}
