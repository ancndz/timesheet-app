package ru.ancndz.timeapp.ui.component.userform;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ancndz.timeapp.security.AuthorizationService;
import ru.ancndz.timeapp.security.ValidationException;
import ru.ancndz.timeapp.ui.view.AppInfoView;
import ru.ancndz.timeapp.user.UserService;
import ru.ancndz.timeapp.user.domain.Role;
import ru.ancndz.timeapp.user.domain.User;
import ru.ancndz.timeapp.user.domain.UserInfo;

import java.time.LocalDate;

/**
 * Компонент формы нового пользователя.
 *
 * @author Anton Utkaev
 * @since 2024.05.14
 */
public class NewUserFormComponent extends AbstractUserFormComponent {

    public NewUserFormComponent(final AuthorizationService authorizationService,
            final PasswordEncoder passwordEncoder,
            final UserService userService) {
        super(userService);

        final User user = getCurrentUser();
        userBinder.setBean(user);
        userInfoBinder.setBean(user.getUserInfo());

        regDateField.setVisible(false);
        regDateField.setValue(LocalDate.now());
        final PasswordField passwordField = new PasswordField(getTranslation("app.field.user.password"),
                getTranslation("app.field.user.password.placeholder"));
        passwordField.setRequired(true);
        final PasswordField passwordFieldRepeat =
                new PasswordField("", getTranslation("app.field.user.confirm-password.placeholder"));
        passwordFieldRepeat.setRequired(true);
        final Checkbox iAmWorker = new Checkbox(getTranslation("app.field.user.worker-role"));

        formLayout.add(passwordField, passwordFieldRepeat, iAmWorker);

        userBinder.forField(passwordFieldRepeat)
                .asRequired()
                .withValidator(password -> password.equals(passwordField.getValue()),
                        getTranslation("app.error.passwords-not-match"))
                .bind(User::getPassword, (details, password) -> details.setPassword(passwordEncoder.encode(password)));

        final Button registerButton = new Button(getTranslation("app.button.register"), event -> {
            event.getSource().setEnabled(false);
            if (userInfoBinder.writeBeanIfValid(userInfoBinder.getBean())
                    && userBinder.writeBeanIfValid(userBinder.getBean())) {
                try {
                    userBinder.getBean().addRole(authorizationService.getAuthority(Role.USER));
                    if (iAmWorker.getValue()) {
                        userBinder.getBean().addRole(authorizationService.getAuthority(Role.WORKER));
                    }
                    userService.saveUser(userBinder.getBean());
                    event.getSource().getUI().ifPresent(ui -> ui.navigate(AppInfoView.class));
                } catch (ValidationException e) {
                    Notification.show(String.join(", ", e.getErrors()), 3000, Notification.Position.BOTTOM_CENTER);
                }
            } else {
                event.getSource().setEnabled(true);
            }
        });
        add(registerButton);
    }

    @Override
    protected User getCurrentUser() {
        return User.newUser().withUserInfo(UserInfo.newUserInfo().withRegDate(LocalDate.now()).build()).build();
    }
}
