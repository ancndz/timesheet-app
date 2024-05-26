package ru.ancndz.timeapp.ui.component.userform;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ancndz.timeapp.security.ValidationException;
import ru.ancndz.timeapp.user.UserService;
import ru.ancndz.timeapp.user.domain.User;

/**
 * Компонент формы текущего пользователя.
 *
 * @author Anton Utkaev
 * @since 2024.04.29
 */
public class CurrentUserFormComponent extends AbstractUserFormComponent {

    private static final Logger log = LoggerFactory.getLogger(CurrentUserFormComponent.class);

    private User currentUser = null;

    public CurrentUserFormComponent(final UserService userService, final AuthenticationContext authenticationContext) {
        super(userService);

        authenticationContext.getAuthenticatedUser(User.class).ifPresent(scheduleUserDetails -> {
            currentUser = scheduleUserDetails;
            userBinder.setBean(scheduleUserDetails);
            userInfoBinder.setBean(scheduleUserDetails.getUserInfo());
            regDateField.setValue(scheduleUserDetails.getUserInfo().getRegDate());
        });

        userInfoBinder.setReadOnly(true);
        userBinder.setReadOnly(true);

        final Button cancelButton = new Button(getTranslation("app.button.cancel"), VaadinIcon.CLOSE.create());
        final Button saveButton = new Button(getTranslation("app.button.save"), VaadinIcon.DISC.create(), event -> {
            if (userInfoBinder.writeBeanIfValid(userInfoBinder.getBean())
                    && userBinder.writeBeanIfValid(userBinder.getBean())) {
                try {
                    log.info("Saving user details: {}", userBinder.getBean());
                    userBinder.getBean()
                            .setPassword(userService.findUserById(userBinder.getBean().getId()).getPassword());
                    userService.saveUser(userBinder.getBean());
                    userInfoBinder.setReadOnly(true);
                    userBinder.setReadOnly(true);
                    event.getSource().setVisible(false);
                    cancelButton.setVisible(false);
                } catch (ValidationException e) {
                    Notification.show(String.join(", ", e.getErrors()), 3000, Notification.Position.BOTTOM_CENTER);
                }
            } else {
                log.warn("Validation failed");
            }
        });
        saveButton.setVisible(false);

        cancelButton.addClickListener(event -> {
            userInfoBinder.setReadOnly(true);
            userBinder.setReadOnly(true);
            saveButton.setVisible(false);
            event.getSource().setVisible(false);
        });
        cancelButton.setVisible(false);

        final Button editButton = new Button(getTranslation("app.button.edit"), VaadinIcon.EDIT.create(), event -> {
            userInfoBinder.setReadOnly(false);
            userBinder.setReadOnly(false);
            saveButton.setVisible(true);
            cancelButton.setVisible(true);
        });

        final HorizontalLayout buttonsLayout = new HorizontalLayout(editButton, saveButton, cancelButton);
        add(buttonsLayout);
    }

    @Override
    protected User getCurrentUser() {
        if (currentUser == null) {
            throw new IllegalStateException("Current user is not set");
        }
        return currentUser;
    }

}
