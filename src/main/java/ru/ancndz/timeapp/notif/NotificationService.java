package ru.ancndz.timeapp.notif;

import ru.ancndz.timeapp.coop.domain.CoopNotification;
import ru.ancndz.timeapp.coop.domain.CooperateInfo;
import ru.ancndz.timeapp.notif.domain.CommonNotification;
import ru.ancndz.timeapp.user.domain.User;

import java.util.List;

/**
 * Сервис уведомлений.
 *
 * @author Anton Utkaev
 * @since 2024.05.18
 */
public interface NotificationService {

    /**
     * Поиск уведомления по айди.
     *
     * @param id
     *            айди уведомления
     * @return уведомление
     */
    CommonNotification findById(String id);

    /**
     * Удаление уведомления по айди.
     *
     * @param id
     *            айди уведомления
     */
    void deleteNotificationById(String id);

    /**
     * Получение активных уведомлений для пользователя.
     *
     * @param addressee
     *            пользователь
     * @return список уведомлений
     */
    List<CommonNotification> getActiveNotifications(User addressee);

    /**
     * Проверка наличия активных уведомлений для пользователя.
     *
     * @param addressee
     *            пользователь
     * @return наличие уведомлений
     */
    boolean hasActiveNotifications(User addressee);

    /**
     * Принять сотрудничество.
     *
     * @param notificationId
     *            айди уведомления
     */
    void acceptCooperation(String notificationId);

    /**
     * Получение уведомлений о сотрудничестве.
     *
     * @param cooperateInfo
     *            информация о сотрудничестве
     * @return список уведомлений
     */
    List<CoopNotification> obtainCoopNotification(CooperateInfo cooperateInfo);
}
