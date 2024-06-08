package ru.ancndz.timeapp.ui.view;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ancndz.timeapp.security.UserRoleService;
import ru.ancndz.timeapp.ui.component.userform.NewUserFormComponent;
import ru.ancndz.timeapp.user.UserService;

/**
 * Представление регистрации нового пользователя.
 *
 * @author Anton Utkaev
 * @since 2024.05.13
 */
@SpringComponent
@RouteScope
@Route(value = RegisterView.LAYOUT_ROUTE, layout = MainLayout.class)
@AnonymousAllowed
public class RegisterView extends Composite<VerticalLayout> implements HasDynamicTitle, BeforeEnterObserver {

    public static final String LAYOUT_ROUTE = "register";

    public static final String LAYOUT_TITLE = "app.view-title.register-view";

    private final transient AuthenticationContext authenticationContext;

    public RegisterView(final UserRoleService userRoleService,
            final AuthenticationContext authenticationContext,
            final PasswordEncoder passwordEncoder,
            final UserService userService) {
        this.authenticationContext = authenticationContext;
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);

        getContent().add(new NewUserFormComponent(userRoleService, passwordEncoder, userService));
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (authenticationContext.isAuthenticated()) {
            event.forwardTo(AppInfoView.class);
        }
    }

    @Override
    public String getPageTitle() {
        return getTranslation(LAYOUT_TITLE);
    }

}
