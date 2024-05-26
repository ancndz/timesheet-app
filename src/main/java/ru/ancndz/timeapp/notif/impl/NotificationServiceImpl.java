package ru.ancndz.timeapp.notif.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.ancndz.timeapp.coop.domain.CoopNotification;
import ru.ancndz.timeapp.coop.domain.CooperateInfo;
import ru.ancndz.timeapp.coop.domain.QCoopNotification;
import ru.ancndz.timeapp.coop.domain.repo.CoopNotificationRepository;
import ru.ancndz.timeapp.core.StoreContext;
import ru.ancndz.timeapp.core.StoreService;
import ru.ancndz.timeapp.notif.NotificationService;
import ru.ancndz.timeapp.notif.domain.CommonNotification;
import ru.ancndz.timeapp.notif.domain.repo.CommonNotificationRepository;
import ru.ancndz.timeapp.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Реализация сервиса уведомлений.
 *
 * @author Anton Utkaev
 * @since 2024.05.18
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private final CommonNotificationRepository commonNotificationRepository;

    private final CoopNotificationRepository coopNotificationRepository;

    private final StoreService storeService;

    public NotificationServiceImpl(final CommonNotificationRepository commonNotificationRepository,
            CoopNotificationRepository coopNotificationRepository,
            final StoreService storeService) {
        this.commonNotificationRepository = commonNotificationRepository;
        this.coopNotificationRepository = coopNotificationRepository;
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
        return commonNotificationRepository.findAllByUserIdAndArchivedAtIsNullOrderByCreatedAtDesc(addressee.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveNotifications(final User addressee) {
        return commonNotificationRepository.existsByUserIdAndArchivedAtIsNull(addressee.getId());
    }

    @Override
    @Transactional
    public void acceptCooperation(String notificationId) {
        final CoopNotification commonNotification =
                ((CoopNotification) commonNotificationRepository.findById(notificationId)
                        .orElseThrow(() -> new IllegalArgumentException("Notification not found")));

        commonNotification.getCooperateInfo().setActive(true);
        commonNotification.setArchivedAt(LocalDateTime.now());

        storeService.store(new StoreContext(commonNotification, commonNotification.getCooperateInfo()));
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<CoopNotification> obtainCoopNotification(CooperateInfo cooperateInfo) {
        QCoopNotification qCoopNotification = QCoopNotification.coopNotification;
        return StreamSupport
                .stream(coopNotificationRepository.findAll(qCoopNotification.cooperateInfo.eq(cooperateInfo))
                        .spliterator(), false)
                .toList();
    }

}
