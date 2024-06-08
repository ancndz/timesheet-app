package ru.ancndz.timeapp.user.impl;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.ancndz.timeapp.core.BeforeStoreEvent;
import ru.ancndz.timeapp.notif.NotificationService;
import ru.ancndz.timeapp.notif.domain.NotificationTypeSystemName;
import ru.ancndz.timeapp.user.domain.User;

import java.util.List;

/**
 * Обработчик создания нового пользователя.
 *
 * @author Anton Utkaev
 * @since 2024.06.05
 */
@Component
public class NewUserHandler {

    private final NotificationService notificationService;

    public NewUserHandler(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener(BeforeStoreEvent.class)
    public void handleNewUser(final BeforeStoreEvent event) {

        final List<User> newUsers = event.getStoreContext()
                .getObjects()
                .stream()
                .filter(User.class::isInstance)
                .map(User.class::cast)
                .filter(User::isNew)
                .toList();

        newUsers.forEach(user -> {
            notificationService.createNotification(null,
                    user.getUserInfo(),
                    NotificationTypeSystemName.NEW_REG,
                    event.getStoreContext());
        });

    }
}
