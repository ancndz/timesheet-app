package ru.ancndz.timeapp.security;

/**
 * Валидатор объектов.
 *
 * @author Anton Utkaev
 * @since 2024.05.14
 */
public interface DomainValidator {

    /**
     * Валидация объекта.
     *
     * @param context
     *            контекст валидации
     * @throws ValidationException
     *             исключение валидации
     */
    void validate(ValidationContext context) throws ValidationException;

}
