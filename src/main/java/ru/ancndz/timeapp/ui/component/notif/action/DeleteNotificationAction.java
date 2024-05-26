package ru.ancndz.timeapp.ui.component.notif.action;

import static ru.ancndz.timeapp.ui.component.notif.action.NotificationActionSystemName.DELETE_ACTION_NAME;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.ancndz.timeapp.notif.NotificationService;
import ru.ancndz.timeapp.ui.component.notif.CommonNotificationComponent;

import java.util.function.Consumer;

/**
 * Действие удаления уведомления.
 *
 * @author Anton Utkaev
 * @since 2024.05.19
 */
@Component
public class DeleteNotificationAction implements NotificationAction {

    private static final Logger log = LoggerFactory.getLogger(DeleteNotificationAction.class);

    private final NotificationService notificationService;

    public DeleteNotificationAction(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public String getActionSystemName() {
        return DELETE_ACTION_NAME;
    }

    @Override
    public String getActionLabelProperty() {
        return "app.notification.action.delete";
    }

    @Override
    public ComponentEventListener<ClickEvent<MenuItem>> getAction() {
        return event -> {
            event.getSource()
                    .getContextMenu()
                    .getId()
                    .ifPresentOrElse(notificationService::deleteNotificationById,
                            () -> log.error("Notification id is not present for delete action"));
        };
    }

    @Override
    public <T extends CommonNotificationComponent> Consumer<T> getAfterAction() {
        return notificationComponent -> {
            notificationComponent.removeFromParent();
            Notification.show(notificationComponent.getTranslation("app.notification.deleted"),
                    3000,
                    Notification.Position.BOTTOM_START);
        };
    }

    @Override
    public Consumer<MenuItem> getMenuItemPostProcessor() {
        return item -> item.addClassNames(LumoUtility.TextColor.ERROR);
    }
}
