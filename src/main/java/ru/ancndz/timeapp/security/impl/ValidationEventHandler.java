package ru.ancndz.timeapp.security.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.ancndz.timeapp.core.BeforeStoreEvent;
import ru.ancndz.timeapp.security.DomainValidator;
import ru.ancndz.timeapp.security.ValidationContext;
import ru.ancndz.timeapp.security.ValidationException;

import java.util.List;

/**
 * Обработчик событий валидации.
 *
 * @author Anton Utkaev
 * @since 2024.05.26
 */
@Component
public class ValidationEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationEventHandler.class);

    private final List<DomainValidator> validators;

    public ValidationEventHandler(final List<DomainValidator> validators) {
        this.validators = validators;
    }

    /**
     * Обработка события перед сохранением.
     *
     * @param event
     *            событие
     * @throws ValidationException
     *             исключение валидации
     */
    @EventListener(BeforeStoreEvent.class)
    public void handleBeforeStoreEvent(final BeforeStoreEvent event) throws ValidationException {
        final ValidationContext validationContext = new ValidationContext(event.getStoreContext().getObjects(),
                SecurityContextHolder.getContext().getAuthentication());
        LOG.info("Validation start: {}", validationContext);
        for (DomainValidator validator : validators) {
            validator.validate(validationContext);
        }
        if (validationContext.hasErrors()) {
            LOG.info("Validation errors: {}", validationContext.getErrors());
            throw new ValidationException(validationContext.getErrors());
        }
    }
}
