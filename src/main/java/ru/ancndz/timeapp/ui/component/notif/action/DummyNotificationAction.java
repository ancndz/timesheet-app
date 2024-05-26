package ru.ancndz.timeapp.ui.component.notif.action;

import static ru.ancndz.timeapp.ui.component.notif.action.NotificationActionSystemName.DUMMY_ACTION_NAME;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import ru.ancndz.timeapp.ui.component.notif.CommonNotificationComponent;

/**
 * Действие для тестирования уведомлений.
 *
 * @author Anton Utkaev
 * @since 2024.05.19
 */
@Component
@Profile("dev")
public class DummyNotificationAction implements NotificationAction {

    private static final Logger log = LoggerFactory.getLogger(DummyNotificationAction.class);

    @Override
    public String getActionSystemName() {
        return DUMMY_ACTION_NAME;
    }

    @Override
    public String getActionLabelProperty() {
        return "app.notification.action.dummy";
    }

    @Override
    public ComponentEventListener<ClickEvent<MenuItem>> getAction() {
        return event -> log.debug(event.getSource().toString() + " clicked");
    }

    @Override
    public <T extends CommonNotificationComponent> Consumer<T> getAfterAction() {
        return null;
    }

    @Override
    public Consumer<MenuItem> getMenuItemPostProcessor() {
        return null;
    }
}
