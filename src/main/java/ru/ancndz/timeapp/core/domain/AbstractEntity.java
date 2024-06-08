package ru.ancndz.timeapp.core.domain;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

/**
 * Абстрактная доменная сущность.
 *
 * @author Anton Utkaev
 * @since 2024.05.14
 */
@MappedSuperclass
public class AbstractEntity implements DomainEntity<String> {

    @Id
    @Nonnull
    private String id;

    @Transient
    private transient boolean deleted = false;

    @Transient
    private transient boolean isNew = false;

    public AbstractEntity() {
    }

    @Override
    @Nonnull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

}
