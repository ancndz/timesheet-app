package ru.ancndz.timeapp.security;

import java.util.List;

/**
 * Исключение валидации.
 *
 * @author Anton Utkaev
 * @since 2024.05.17
 */
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    public ValidationException(final List<String> s) {
        super(String.join(";", s));
        errors = s;
    }

    public List<String> getErrors() {
        return errors;
    }
}
