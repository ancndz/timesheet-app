package ru.ancndz.timeapp.core;

import org.springframework.context.ApplicationEvent;

/**
 * Событие до сохранения.
 *
 * @author Anton Utkaev
 * @since 2024.05.11
 */
public class BeforeStoreEvent extends ApplicationEvent {

    private final StoreContext storeContext;

    public BeforeStoreEvent(final Object source, final StoreContext storeContext) {
        super(source);
        this.storeContext = storeContext;
    }

    public StoreContext getStoreContext() {
        return storeContext;
    }
}
