package ru.ancndz.timeapp.ui.component.notif.action;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.MenuItem;
import jakarta.annotation.Nullable;
import ru.ancndz.timeapp.ui.component.notif.CommonNotificationComponent;

import java.util.function.Consumer;

/**
 * Действие уведомления.
 *
 * @author Anton Utkaev
 * @since 2024.05.19
 */
public interface NotificationAction {

    String getActionSystemName();

    String getActionLabelProperty();

    ComponentEventListener<ClickEvent<MenuItem>> getAction();

    @Nullable
    <T extends CommonNotificationComponent> Consumer<T> getAfterAction();

    @Nullable
    Consumer<MenuItem> getMenuItemPostProcessor();
}
