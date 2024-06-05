package ru.ancndz.timeapp.notif;

import ru.ancndz.timeapp.core.StoreContext;
import ru.ancndz.timeapp.notif.domain.CommonNotification;
import ru.ancndz.timeapp.notif.domain.NotificationType;
import ru.ancndz.timeapp.user.domain.User;
import ru.ancndz.timeapp.user.domain.UserInfo;

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
     * Поиск типа уведомления по системному имени.
     *
     * @param systemName
     *            системное имя типа уведомления
     * @return тип уведомления
     */
    NotificationType findNotificationTypeBySystemName(String systemName);

    /**
     * Создание уведомления.
     *
     * @param sender
     *            отправитель
     * @param addressee
     *            получатель
     * @param type
     *            тип уведомления
     * @param storeContext
     *            контекст хранения
     */
    void createNotification(UserInfo sender, UserInfo addressee, String type, StoreContext storeContext);

    /**
     * Создание уведомления.
     *
     * @param sender
     *            отправитель
     * @param addressee
     *            получатель
     * @param type
     *            тип уведомления
     * @param storeContext
     *            контекст хранения
     */
    void createNotification(UserInfo sender, UserInfo addressee, NotificationType type, StoreContext storeContext);
}
