package ru.ancndz.timeapp.ui.component.notif.action;

import static ru.ancndz.timeapp.ui.component.notif.action.NotificationActionSystemName.ACCEPT_COOP_ACTION_NAME;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.ancndz.timeapp.notif.NotificationService;
import ru.ancndz.timeapp.ui.component.notif.CommonNotificationComponent;

import java.util.function.Consumer;

/**
 * Действие принятия уведомления о сотрудничестве.
 *
 * @author Anton Utkaev
 * @since 2024.05.20
 */
@Component
public class AcceptCoopNotificationAction implements NotificationAction {

    private static final Logger log = LoggerFactory.getLogger(AcceptCoopNotificationAction.class);

    private final NotificationService notificationService;

    public AcceptCoopNotificationAction(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public String getActionSystemName() {
        return ACCEPT_COOP_ACTION_NAME;
    }

    @Override
    public String getActionLabelProperty() {
        return "app.notification.action.accept-coop";
    }

    @Override
    public ComponentEventListener<ClickEvent<MenuItem>> getAction() {
        return event -> {
            event.getSource().getContextMenu().getId().ifPresentOrElse(notificationService::acceptCooperation, () -> {
                log.error("Notification id is not present for accept-coop action");
            });
        };
    }

    @Nullable
    @Override
    public <T extends CommonNotificationComponent> Consumer<T> getAfterAction() {
        return notificationComponent -> {
            notificationComponent.removeFromParent();
            Notification.show(notificationComponent.getTranslation("app.notification.accepted-coop"),
                    3000,
                    Notification.Position.BOTTOM_START);
        };
    }

    @Nullable
    @Override
    public Consumer<MenuItem> getMenuItemPostProcessor() {
        return item -> item.addClassNames(LumoUtility.TextColor.SUCCESS);
    }
}
