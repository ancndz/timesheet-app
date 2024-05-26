package ru.ancndz.timeapp.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ancndz.timeapp.core.BeforeStoreEvent;
import ru.ancndz.timeapp.core.StoreContext;
import ru.ancndz.timeapp.core.StoreService;
import ru.ancndz.timeapp.core.domain.DomainEntity;
import ru.ancndz.timeapp.core.domain.SoftDeletionSupported;

import java.beans.Introspector;

/**
 * Реализация сервиса для хранения контекста.
 *
 * @author Anton Utkaev
 * @since 2024.05.11
 */
@Service
public class StoreServiceImpl implements StoreService {

    private static final Logger log = LoggerFactory.getLogger(StoreServiceImpl.class);

    private final ApplicationContext applicationContext;

    public StoreServiceImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    @Transactional
    public void store(final StoreContext context) {
        log.info("Storing context: {}", context);
        log.info("Publishing BeforeStoreEvent");
        applicationContext.publishEvent(new BeforeStoreEvent(this, context));

        context.getObjects().forEach(this::saveEntity);
    }

    /**
     * Сохранение сущности.
     *
     * @param entity
     *            сущность
     */
    private <T extends Persistable<ID>, ID> void saveEntity(final T entity) {
        final CrudRepository<T, ?> repository = resolveRepository((Class<T>) entity.getClass());
        if (entity instanceof DomainEntity<?>) {
            if (((DomainEntity<?>) entity).isDeleted() && !(entity instanceof SoftDeletionSupported)) {
                log.debug("Entity is marked as deleted: {}", entity);
                repository.delete(entity);
                return;
            }
        }
        log.debug("Saving entity: {}", entity);
        repository.save(entity);
    }

    /**
     * Получение репозитория для сущности.
     *
     * @param entityClass
     *            класс сущности
     * @param <T>
     *            тип сущности
     * @param <ID>
     *            тип идентификатора
     * @return репозиторий
     */
    @SuppressWarnings("unchecked")
    private <T extends Persistable<ID>, ID> CrudRepository<T, ID> resolveRepository(Class<? super T> entityClass) {
        return (CrudRepository<T, ID>) applicationContext
                .getBean(Introspector.decapitalize(entityClass.getSimpleName()) + "Repository");
    }
}
