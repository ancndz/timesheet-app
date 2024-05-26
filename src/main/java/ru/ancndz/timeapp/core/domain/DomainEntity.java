package ru.ancndz.timeapp.core.domain;

import org.springframework.data.domain.Persistable;

/**
 * Доменная сущность.
 *
 * @author Anton Utkaev
 * @since 2024.05.12
 */
public interface DomainEntity<ID> extends Persistable<ID> {

    boolean isDeleted();

}
