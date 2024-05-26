package ru.ancndz.timeapp.security;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Контекст валидации.
 */
public class ValidationContext {

    /**
     * Сущности.
     */
    private final List<?> entities;

    /**
     * Текущий пользователь.
     */
    private final Principal user;

    /**
     * Ошибки.
     */
    private final List<String> errors = new ArrayList<>();

    public ValidationContext(List<?> entities, Principal user) {
        this.entities = entities;
        this.user = user;
    }

    public List<?> getEntities() {
        return entities;
    }

    public Principal getUser() {
        return user;
    }

    public void addError(String error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "ValidationContext{" + "entities=" + entities + ", user=" + user + ", errors=" + errors + '}';
    }
}
