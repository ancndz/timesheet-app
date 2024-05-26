package ru.ancndz.timeapp.ui.component.notif.action.service;

import ru.ancndz.timeapp.ui.component.notif.CommonNotificationComponent;

import java.util.List;

/**
 * Провайдер действий для уведомлений.
 *
 * @author Anton Utkaev
 * @since 2024.05.19
 */
public interface NotificationActionProvider {

    /**
     * Получение системного имени действия.
     *
     * @return системное имя действия
     */
    List<String> getActionNames();

    /**
     * Получение класса поддерживаемого компонента.
     *
     * @return класс поддерживаемого компонента
     */
    Class<? extends CommonNotificationComponent> getSupportedComponentClass();
}
