package ru.ancndz.timeapp.ui.component.userform;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.theme.lumo.LumoUtility;
import ru.ancndz.timeapp.user.PhoneNumberConverter;
import ru.ancndz.timeapp.user.UserService;
import ru.ancndz.timeapp.user.domain.User;
import ru.ancndz.timeapp.user.domain.UserInfo;

/**
 * Абстрактный компонент формы пользователя.
 *
 * @author Anton Utkaev
 * @since 2024.04.29
 */
public abstract class AbstractUserFormComponent extends VerticalLayout {

    protected final FormLayout formLayout;

    protected final TextField nameField;

    protected final TextField infoNameField;

    protected final EmailField emailField;

    protected final DatePicker regDateField;

    protected final TextField phoneNumberField;

    protected final Binder<UserInfo> userInfoBinder;

    protected final Binder<User> userBinder;

    public AbstractUserFormComponent(UserService userService) {

        addClassNames(LumoUtility.Padding.XSMALL, LumoUtility.Margin.XSMALL);
        setMaxWidth("700px");

        formLayout = new FormLayout();
        nameField =
                new TextField(getTranslation("app.field.user.name"), getTranslation("app.field.user.name.placeholder"));
        nameField.setRequired(true);
        infoNameField = new TextField(getTranslation("app.field.user-info.name"),
                getTranslation("app.field.user-info.full-name.placeholder"));
        infoNameField.setRequired(true);
        emailField = new EmailField(getTranslation("app.field.user-info.email"),
                getTranslation("app.field.user-info.email.placeholder"));
        emailField.setRequired(true);
        regDateField = new DatePicker(getTranslation("app.field.user-info.reg-date"));
        phoneNumberField = new TextField(getTranslation("app.field.user-info.phone-number"),
                getTranslation("app.field.user-info.phone-number.placeholder"));

        formLayout.add(nameField, infoNameField, emailField, phoneNumberField, regDateField);

        userInfoBinder = new Binder<>(UserInfo.class);
        userInfoBinder.forField(infoNameField).asRequired().bind(UserInfo::getName, UserInfo::setName);
        userInfoBinder.forField(regDateField).bind(UserInfo::getRegDate, null);

        userInfoBinder.forField(emailField)
                .asRequired()
                .withValidator(email -> !userService.existsUserByEmail(getCurrentUser().getUserInfo(), email),
                        getTranslation("app.error.email-exists"))
                .bind(UserInfo::getEmail, UserInfo::setEmail);

        userInfoBinder.forField(phoneNumberField)
                .withValidator(
                        phoneNumber -> phoneNumber == null
                                || phoneNumber.isEmpty()
                                || PhoneNumberConverter.isValidPhoneNumber(phoneNumber),
                        getTranslation("app.error.phone-number-invalid"))
                .withValidator(
                        phoneNumber -> phoneNumber == null
                                || phoneNumber.isEmpty()
                                || !userService.existsUserByPhoneNumber(getCurrentUser().getUserInfo(), phoneNumber),
                        getTranslation("app.error.phone-number-exists"))
                .bind(UserInfo::getPhoneNumber, UserInfo::setPhoneNumber);

        userBinder = new Binder<>(User.class);
        userBinder.forField(nameField)
                .asRequired()
                .withValidator(name -> name.matches("[a-zA-Z0-9_!@#$%^&*()]+"), getTranslation("app.error.name-not-latin"))
                .withValidator(name -> !userService.existsUserByName(getCurrentUser(), name),
                        getTranslation("app.error.name-exists"))
                .bind(User::getUsername, User::setUsername);

        add(formLayout);
    }

    protected abstract User getCurrentUser();

}
