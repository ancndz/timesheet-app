package ru.ancndz.timeapp.coop.impl;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.ancndz.timeapp.coop.domain.CooperateInfo;
import ru.ancndz.timeapp.core.BeforeStoreEvent;
import ru.ancndz.timeapp.core.StoreContext;
import ru.ancndz.timeapp.notif.NotificationService;
import ru.ancndz.timeapp.notif.domain.NotificationType;
import ru.ancndz.timeapp.notif.domain.NotificationTypeSystemName;

/**
 * Обработчик сохранения информации о сотрудничестве.
 *
 * @author Anton Utkaev
 * @since 2024.05.18
 */
@Component
public class CooperationSaveNotificationHandler {

    private final NotificationService notificationService;

    public CooperationSaveNotificationHandler(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Обработка сохранения информации о сотрудничестве.
     *
     * @param event
     *            событие сохранения
     */
    @EventListener(BeforeStoreEvent.class)
    public void onCooperationSave(final BeforeStoreEvent event) {
        final StoreContext storeContext = event.getStoreContext();

        final var newCoops = storeContext.getObjects()
                .stream()
                .filter(CooperateInfo.class::isInstance)
                .map(CooperateInfo.class::cast)
                .filter(CooperateInfo::isNew)
                .filter(cooperateInfo -> !cooperateInfo.isActive())
                .toList();

        final var deletedCoops = storeContext.getObjects()
                .stream()
                .filter(CooperateInfo.class::isInstance)
                .map(CooperateInfo.class::cast)
                .filter(cooperateInfo -> cooperateInfo.getArchivedAt() != null && cooperateInfo.isActive())
                .toList();

        final NotificationType newCoopNotificationType =
                notificationService.findNotificationTypeBySystemName(NotificationTypeSystemName.NEW_COOP);
        newCoops.forEach(cooperateInfo -> {
            notificationService.createNotification(cooperateInfo.getWorker(),
                    cooperateInfo.getClient(),
                    newCoopNotificationType,
                    storeContext);
        });

        final NotificationType deletedCoopNotificationType =
                notificationService.findNotificationTypeBySystemName(NotificationTypeSystemName.COOP_DELETED);
        deletedCoops.forEach(cooperateInfo -> {
            notificationService.createNotification(cooperateInfo.getWorker(),
                    cooperateInfo.getClient(),
                    deletedCoopNotificationType,
                    storeContext);
        });
    }

}
