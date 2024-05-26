package ru.ancndz.timeapp.coop;

import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.ancndz.timeapp.coop.domain.CoopNotification;
import ru.ancndz.timeapp.coop.domain.CooperateInfo;
import ru.ancndz.timeapp.core.BeforeStoreEvent;
import ru.ancndz.timeapp.core.StoreContext;
import ru.ancndz.timeapp.notif.NotificationService;
import ru.ancndz.timeapp.notif.domain.CommonNotification;

import java.util.Locale;

/**
 * Обработчик сохранения информации о сотрудничестве.
 *
 * @author Anton Utkaev
 * @since 2024.05.18
 */
@Component
public class CooperationSaveNotificationHandler {

    private final NotificationService notificationService;

    private final MessageSource messageSource;

    public CooperationSaveNotificationHandler(final NotificationService notificationService,
            final MessageSource messageSource) {
        this.notificationService = notificationService;
        this.messageSource = messageSource;
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

        newCoops.forEach(cooperateInfo -> {
            final String message =
                    messageSource.getMessage("app.notification.type.new-coop", null, Locale.getDefault());
            final CoopNotification coopNotification = CoopNotification.newCoopNotification()
                    .withUser(cooperateInfo.getClient())
                    .withSender(cooperateInfo.getWorker())
                    .withMessage(message)
                    .withCooperateInfo(cooperateInfo)
                    .build();
            storeContext.add(coopNotification);
        });

        deletedCoops.forEach(cooperateInfo -> {
            final String message =
                    messageSource.getMessage("app.notification.type.coop-deleted", null, Locale.getDefault());
            final CommonNotification coopNotification = CommonNotification.newNotification()
                    .withUser(cooperateInfo.getClient())
                    .withSender(cooperateInfo.getWorker())
                    .withMessage(message)
                    .build();
            storeContext.add(coopNotification);
        });
    }

    /**
     * Обработка удаления информации о сотрудничестве.
     *
     * @param event
     *            событие сохранения
     */
    @EventListener(BeforeStoreEvent.class)
    public void onCooperationDelete(final BeforeStoreEvent event) {
        final StoreContext storeContext = event.getStoreContext();

        final var deletedCoops = storeContext.getObjects()
                .stream()
                .filter(CooperateInfo.class::isInstance)
                .map(CooperateInfo.class::cast)
                .filter(cooperateInfo -> cooperateInfo.getArchivedAt() != null)
                .toList();

        deletedCoops.forEach(cooperateInfo -> {
            final String message =
                    messageSource.getMessage("app.notification.type.coop-deleted", null, Locale.getDefault());
            final CommonNotification commonNotification = CommonNotification.newNotification()
                    .withUser(cooperateInfo.getClient())
                    .withSender(cooperateInfo.getWorker())
                    .withMessage(message)
                    .build();
            notificationService.obtainCoopNotification(cooperateInfo).forEach(coopNotificationToDelete -> {
                coopNotificationToDelete.setDeleted(true);
                storeContext.add(coopNotificationToDelete);
            });
            storeContext.add(commonNotification);
        });
    }
}
