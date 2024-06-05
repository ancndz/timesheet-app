package ru.ancndz.timeapp.ui.component.notif;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import ru.ancndz.timeapp.notif.domain.CommonNotification;
import ru.ancndz.timeapp.ui.component.notif.action.NotificationAction;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;

/**
 * Общий компонент уведомления.
 *
 * @author Anton Utkaev
 * @since 2024.05.18
 */
public class CommonNotificationComponent extends Div implements HasComponents, HasStyle, HasTheme {

    private ContextMenu actionMenu;

    private final Button menuButton;

    private final Text messageText;

    private final Text infoText;

    private final HorizontalLayout layout;

    /**
     * Конструктор.
     */
    protected CommonNotificationComponent() {
        setVisible(false);
        addClassName("notification-component");
        addThemeName("contrast");

        messageText = new Text("");
        final Div messageDiv = new Div(messageText);
        messageDiv.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.BOLD);

        infoText = new Text("");
        final Div infoDiv = new Div(infoText);
        infoDiv.addClassNames(LumoUtility.FontSize.SMALL);

        menuButton = new Button(VaadinIcon.BULLETS.create());
        menuButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        menuButton.addClassNames(LumoUtility.Margin.Left.XLARGE);

        layout = new HorizontalLayout(new Div(messageDiv, infoDiv), menuButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        add(layout);
    }

    /**
     * Установить уведомление.
     *
     * @param notification
     *            уведомление
     */
    public void setNotification(final CommonNotification notification) {
        if (notification != null) {
            if (notification.getMessage() != null) {
                messageText.setText(notification.getMessage());
            } else {
                messageText.setText(getTranslation(notification.getType().getMessage()));
            }
            if (notification.getSender() != null) {
                infoText.setText(getTranslation("app.message.notification.subtext",
                        notification.getSender().getName(),
                        notification.getCreatedAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))));
            } else {
                infoText.setText(getTranslation("app.message.notification.subtext.short",
                        notification.getCreatedAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))));

            }
        }
    }

    public ContextMenu getActionMenu() {
        return actionMenu;
    }

    protected void setActionMenu(ContextMenu actionMenu) {
        this.actionMenu = actionMenu;
    }

    public Button getMenuButton() {
        return menuButton;
    }

    public HorizontalLayout getLayout() {
        return layout;
    }

    protected void addIcon(final Icon icon) {
        layout.addComponentAsFirst(icon);
    }

    /**
     * Билдер для создания компонента.
     */
    public static class CommonBuilder extends AbstractBuilder<CommonNotificationComponent> {

        public static CommonBuilder newBuilder() {
            return new CommonBuilder();
        }

        @Override
        protected CommonNotificationComponent createEmpty() {
            return new CommonNotificationComponent();
        }
    }

    /**
     * Абстрактный билдер.
     *
     * @param <T>
     *            тип компонента
     */
    protected abstract static class AbstractBuilder<T extends CommonNotificationComponent> {

        private Collection<NotificationAction> actions;

        private Icon icon;

        private CommonNotification notification;

        private ContextMenu contextMenu;

        @SuppressWarnings("unchecked")
        protected T createEmpty() {
            return (T) new CommonNotificationComponent();
        }

        public AbstractBuilder<T> withIcon(Icon icon) {
            this.icon = icon;
            return this;
        }

        public AbstractBuilder<T> withNotification(CommonNotification notification) {
            this.notification = notification;
            return this;
        }

        public AbstractBuilder<T> withActions(Collection<NotificationAction> actions) {
            if (!actions.isEmpty()) {
                this.contextMenu = new ContextMenu();
                this.actions = actions;
            }
            return this;
        }

        public T build() {
            if (this.notification == null) {
                throw new IllegalArgumentException("Notification must be set");
            }
            final T notificationComponent = createEmpty();
            notificationComponent.setNotification(this.notification);
            if (this.icon != null) {
                notificationComponent.addIcon(this.icon);
            }

            if (this.contextMenu != null) {
                this.contextMenu.setId(this.notification.getId());
                this.contextMenu.setOpenOnClick(true);
                this.contextMenu.setTarget(notificationComponent.getMenuButton());
                notificationComponent.setActionMenu(this.contextMenu);

                this.actions.forEach(action -> {
                    final MenuItem menuItem =
                            contextMenu.addItem(notificationComponent.getTranslation(action.getActionLabelProperty()),
                                    action.getAction());
                    if (action.getAfterAction() != null) {
                        menuItem.addClickListener(event -> {
                            action.getAfterAction().accept(notificationComponent);
                        });
                    }
                    if (action.getMenuItemPostProcessor() != null) {
                        action.getMenuItemPostProcessor().accept(menuItem);
                    }
                });
            }

            notificationComponent.setVisible(true);
            return notificationComponent;
        }
    }

}
