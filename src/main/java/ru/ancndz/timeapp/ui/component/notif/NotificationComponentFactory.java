package ru.ancndz.timeapp.ui.component.notif;

import com.vaadin.flow.component.icon.VaadinIcon;
import org.springframework.stereotype.Component;
import ru.ancndz.timeapp.coop.domain.CoopNotification;
import ru.ancndz.timeapp.notif.domain.CommonNotification;
import ru.ancndz.timeapp.notif.domain.NotificationType;
import ru.ancndz.timeapp.ui.component.notif.action.NotificationAction;
import ru.ancndz.timeapp.ui.component.notif.action.service.NotificationActionProvider;
import ru.ancndz.timeapp.ui.component.notif.action.service.NotificationActionRegistry;
import ru.ancndz.timeapp.ui.component.notif.coop.CoopNotificationComponent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Фабрика компонентов уведомлений.
 *
 * @author Anton Utkaev
 * @since 2024.05.19
 */
@Component
public class NotificationComponentFactory {

    /**
     * Реестр действий уведомлений.
     */
    private final NotificationActionRegistry notificationActionRegistry;

    /**
     * Провайдеры действий для компонентов уведомлений.
     */
    private final Map<Class<? extends CommonNotificationComponent>,
            NotificationActionProvider> notificationActionProviders;

    /**
     * Конструктор.
     * 
     * @param notificationActionRegistry
     * @param notificationActionProviders
     */
    public NotificationComponentFactory(final NotificationActionRegistry notificationActionRegistry,
            final List<NotificationActionProvider> notificationActionProviders) {
        this.notificationActionRegistry = notificationActionRegistry;
        this.notificationActionProviders = notificationActionProviders.stream()
                .collect(Collectors.toMap(NotificationActionProvider::getSupportedComponentClass, Function.identity()));
    }

    /**
     * Создание компонента уведомления.
     *
     * @param notification
     *            уведомление
     * @param <T>
     *            тип уведомления
     * @return компонент уведомления
     */
    public <T extends CommonNotification> CommonNotificationComponent
            createNotificationComponent(final T notification) {
        final NotificationType notificationType = notification.getType();

        return switch (notificationType) {
            case COOPERATION -> createCoopNotificationComponent((CoopNotification) notification);
            default -> createBaseNotificationComponent(notification);
        };
    }

    /**
     * Создание компонента уведомления о сотрудничестве.
     *
     * @param notification
     *            уведомление
     * @param <T>
     *            тип уведомления
     * @return компонент уведомления
     */
    private <T extends CommonNotification> CoopNotificationComponent createCoopNotificationComponent(T notification) {
        final List<NotificationAction> notificationActions = getNotificationActions(CoopNotificationComponent.class);

        return CoopNotificationComponent.CoopBuilder.newBuilder()
                .withIcon(VaadinIcon.CONNECT.create())
                .withNotification(notification)
                .withActions(notificationActions)
                .build();
    }

    /**
     * Создание базового компонента уведомления.
     *
     * @param notification
     *            уведомление
     * @return компонент уведомления
     */
    private CommonNotificationComponent createBaseNotificationComponent(CommonNotification notification) {
        final List<NotificationAction> notificationActions = getNotificationActions(CommonNotificationComponent.class);

        return CommonNotificationComponent.CommonBuilder.newBuilder()
                .withIcon(VaadinIcon.INFO.create())
                .withNotification(notification)
                .withActions(notificationActions)
                .build();
    }

    /**
     * Получение действий уведомления.
     *
     * @param componentClass
     *            класс компонента
     * @return действия уведомления
     */
    private List<NotificationAction>
            getNotificationActions(Class<? extends CommonNotificationComponent> componentClass) {
        return notificationActionProviders.get(componentClass)
                .getActionNames()
                .stream()
                .map(notificationActionRegistry::getAction)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
