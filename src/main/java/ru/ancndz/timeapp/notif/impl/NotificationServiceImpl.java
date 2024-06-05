package ru.ancndz.timeapp.notif.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ancndz.timeapp.core.StoreContext;
import ru.ancndz.timeapp.core.StoreService;
import ru.ancndz.timeapp.notif.NotificationService;
import ru.ancndz.timeapp.notif.domain.CommonNotification;
import ru.ancndz.timeapp.notif.domain.NotificationType;
import ru.ancndz.timeapp.notif.domain.repo.CommonNotificationRepository;
import ru.ancndz.timeapp.notif.domain.repo.NotificationTypeRepository;
import ru.ancndz.timeapp.user.domain.User;
import ru.ancndz.timeapp.user.domain.UserInfo;

import java.util.List;

/**
 * Реализация сервиса уведомлений.
 *
 * @author Anton Utkaev
 * @since 2024.05.18
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private final CommonNotificationRepository commonNotificationRepository;

    private final NotificationTypeRepository notificationTypeRepository;

    private final StoreService storeService;

    public NotificationServiceImpl(final CommonNotificationRepository commonNotificationRepository,
            final NotificationTypeRepository notificationTypeRepository,
            final StoreService storeService) {
        this.commonNotificationRepository = commonNotificationRepository;
        this.notificationTypeRepository = notificationTypeRepository;
        this.storeService = storeService;
    }

    @Override
    @Transactional(readOnly = true)
    public CommonNotification findById(String id) {
        return commonNotificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
    }

    @Override
    @Transactional
    public void deleteNotificationById(String id) {
        final CommonNotification commonNotification = commonNotificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        commonNotification.setDeleted(true);

        storeService.store(new StoreContext(commonNotification));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommonNotification> getActiveNotifications(final User addressee) {
        return commonNotificationRepository
                .findAllByAddresseeIdAndArchivedAtIsNullOrderByCreatedAtDesc(addressee.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveNotifications(final User addressee) {
        return commonNotificationRepository.existsByAddresseeIdAndArchivedAtIsNull(addressee.getId());
    }

    @Override
    public NotificationType findNotificationTypeBySystemName(String systemName) {
        return notificationTypeRepository.findBySystemName(systemName);
    }

    @Override
    public void createNotification(final UserInfo sender,
            final UserInfo addressee,
            final String type,
            final StoreContext storeContext) {
        final NotificationType notificationType = notificationTypeRepository.findBySystemName(type);
        createNotification(sender, addressee, notificationType, storeContext);
    }

    @Override
    public void createNotification(final UserInfo sender,
            final UserInfo addressee,
            final NotificationType type,
            final StoreContext storeContext) {
        final CommonNotification notification =
                CommonNotification.newNotification().withSender(sender).withAddressee(addressee).withType(type).build();
        storeContext.add(notification);
    }

}
