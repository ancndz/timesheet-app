package ru.ancndz.timeapp.ui.view;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import ru.ancndz.timeapp.notif.NotificationService;
import ru.ancndz.timeapp.ui.IconViewContainer;
import ru.ancndz.timeapp.ui.component.notif.NotificationComponentFactory;
import ru.ancndz.timeapp.user.domain.User;

/**
 * Представление уведомлений.
 *
 * @author Anton Utkaev
 * @since 2024.05.18
 */
@SpringComponent
@RouteScope
@Route(value = NotificationEntityView.LAYOUT_ROUTE, layout = MainLayout.class)
@PermitAll
public class NotificationEntityView extends Composite<VerticalLayout> implements HasDynamicTitle, IconViewContainer {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(NotificationEntityView.class);

    public static final String LAYOUT_ROUTE = "notifications";

    public static final String LAYOUT_TITLE = "app.view-title.notifications-view";

    public NotificationEntityView(final NotificationService notificationService,
            final AuthenticationContext authenticationContext,
            final NotificationComponentFactory notificationComponentFactory) {

        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().setSpacing(true);
        getContent().getThemeList().add("spacing-l");

        authenticationContext.getAuthenticatedUser(User.class).ifPresent(user -> {
            if (notificationService.hasActiveNotifications(user)) {
                notificationService.getActiveNotifications(user).forEach(notification -> {
                    final var notificationComponent =  notificationComponentFactory.createNotificationComponent(notification);
                    getContent().add(notificationComponent);
                });
            } else {
                getContent().add(getTranslation("app.notification.no-notifications"));
            }
        });

    }

    @Override
    public String getPageTitle() {
        return getTranslation(LAYOUT_TITLE);
    }

    @Override
    public Icon getIcon() {
        return VaadinIcon.INBOX.create();
    }
}
