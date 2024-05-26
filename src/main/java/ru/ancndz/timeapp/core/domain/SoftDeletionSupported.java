package ru.ancndz.timeapp.core.domain;

/**
 * Интерфейс для поддержки мягкого удаления.
 *
 * @author Anton Utkaev
 * @since 2024.05.21
 */
public interface SoftDeletionSupported {

    void setDeleted(boolean deleted);

    boolean isDeleted();
}
