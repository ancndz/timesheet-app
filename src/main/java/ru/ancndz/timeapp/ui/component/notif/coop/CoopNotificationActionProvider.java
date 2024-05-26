package ru.ancndz.timeapp.ui.component.notif.coop;

import org.springframework.stereotype.Component;

import java.util.List;
import ru.ancndz.timeapp.ui.component.notif.CommonNotificationComponent;
import ru.ancndz.timeapp.ui.component.notif.action.NotificationActionSystemName;
import ru.ancndz.timeapp.ui.component.notif.action.service.NotificationActionProvider;

/**
 * Провайдер действий для уведомлений о сотрудничестве.
 *
 * @author Anton Utkaev
 * @since 2024.05.20
 */
@Component
public class CoopNotificationActionProvider implements NotificationActionProvider {

    @Override
    public List<String> getActionNames() {
        return List.of(NotificationActionSystemName.ACCEPT_COOP_ACTION_NAME, NotificationActionSystemName.DELETE_ACTION_NAME);
    }

    @Override
    public Class<? extends CommonNotificationComponent> getSupportedComponentClass() {
        return CoopNotificationComponent.class;
    }
}
