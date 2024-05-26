package ru.ancndz.timeapp.ui.view;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.security.AuthenticationContext;

/**
 * Представление входа в систему.
 *
 * @author Anton Utkaev
 * @since 2024.05.08
 */
@SpringComponent
@RouteScope
@Route(value = LoginView.LAYOUT_ROUTE)
@AnonymousAllowed
public class LoginView extends Composite<LoginOverlay> implements BeforeEnterObserver, HasDynamicTitle {

    public static final String LAYOUT_ROUTE = "login";

    public static final String LAYOUT_TITLE = "app.view-title.login-view";

    private transient final AuthenticationContext authenticationContext;

    public LoginView(final AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
        getContent().setAction(LAYOUT_ROUTE);

        getContent().setForgotPasswordButtonVisible(false);

        final Button registerButton = new Button(getTranslation("app.button.register"), e -> {
            e.getSource().getUI().ifPresent(ui -> ui.navigate(RegisterView.LAYOUT_ROUTE));
        });
        registerButton.setWidthFull();
        getContent().getFooter().add(registerButton);

        final LoginI18n i18n = LoginI18n.createDefault();

        final LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setTitle(getTranslation("app.login-form.title"));
        i18nForm.setUsername(getTranslation("app.login-form.username"));
        i18nForm.setPassword(getTranslation("app.login-form.password"));
        i18nForm.setSubmit(getTranslation("app.login-form.submit"));
        i18n.setForm(i18nForm);

        final LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setTitle(getTranslation("app.login-error.title"));
        i18nErrorMessage.setMessage(getTranslation("app.login-error.message"));
        i18nErrorMessage.setUsername(getTranslation("app.login-error.username"));
        i18nErrorMessage.setPassword(getTranslation("app.login-error.password"));
        i18n.setErrorMessage(i18nErrorMessage);

        getContent().setI18n(i18n);

        getContent().setOpened(true);
    }

    @Override
    public String getPageTitle() {
        return getTranslation(LAYOUT_TITLE);
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (authenticationContext.isAuthenticated()) {
            getContent().setOpened(false);
            event.forwardTo(AppInfoView.class);
        }

        getContent().setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
