package ru.ancndz.timeapp.ui.component.notif.action.service;

import org.springframework.stereotype.Component;
import ru.ancndz.timeapp.ui.component.notif.action.NotificationAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Реестр действий для уведомлений.
 *
 * @author Anton Utkaev
 * @since 2024.05.19
 */
@Component
public class NotificationActionRegistry {

    private final Map<String, NotificationAction> registry;

    public NotificationActionRegistry(final List<NotificationAction> actions) {
        this.registry = actions.stream()
                .collect(HashMap::new, (map, action) -> map.put(action.getActionSystemName(), action), HashMap::putAll);
    }

    public Optional<NotificationAction> getAction(String actionName) {
        return Optional.ofNullable(registry.get(actionName));
    }
}