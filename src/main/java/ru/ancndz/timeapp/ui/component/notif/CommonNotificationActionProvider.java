package ru.ancndz.timeapp.ui.component.notif;

import org.springframework.stereotype.Component;

import java.util.List;
import ru.ancndz.timeapp.ui.component.notif.action.NotificationActionSystemName;
import ru.ancndz.timeapp.ui.component.notif.action.service.NotificationActionProvider;

/**
 * Провайдер действий для общих уведомлений.
 *
 * @author Anton Utkaev
 * @since 2024.05.20
 */
@Component
public class CommonNotificationActionProvider implements NotificationActionProvider {

    @Override
    public List<String> getActionNames() {
        return List.of(NotificationActionSystemName.DELETE_ACTION_NAME);
    }

    @Override
    public Class<? extends CommonNotificationComponent> getSupportedComponentClass() {
        return CommonNotificationComponent.class;
    }
}
