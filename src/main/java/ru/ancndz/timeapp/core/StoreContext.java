package ru.ancndz.timeapp.core;

import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Контекст сохранения.
 */
public class StoreContext {

    private final List<Persistable<?>> objects = new ArrayList<>();

    public StoreContext() {
    }

    public StoreContext(final Persistable<?> object) {
        add(object);
    }

    public StoreContext(final Persistable<?>... objects) {
        addAll(Arrays.asList(objects));
    }

    public StoreContext(final List<Persistable<?>> objects) {
        addAll(objects);
    }

    public void add(final Persistable<?> object) {
        this.objects.add(object);
    }

    public void addAll(final List<Persistable<?>> objects) {
        this.objects.addAll(objects);
    }

    public List<? extends Persistable<?>> getObjects() {
        return objects;
    }
}