package ru.ancndz.timeapp.ui.controller;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import ru.ancndz.timeapp.coop.CooperateInfoService;
import ru.ancndz.timeapp.ui.view.CooperationView;
import ru.ancndz.timeapp.ui.view.LoginView;
import ru.ancndz.timeapp.user.domain.Role;
import ru.ancndz.timeapp.user.domain.User;

import java.util.Optional;

/**
 * Контроллер сотрудничества.
 *
 * @author Anton Utkaev
 * @since 2024.05.14
 */
@SpringComponent
@RouteScope
@Route("cooperate/create")
@RolesAllowed(Role.WORKER)
public class CooperateController extends Composite<VerticalLayout>
        implements HasDynamicTitle, HasUrlParameter<String>, BeforeEnterObserver {

    private final CooperateInfoService cooperateInfoService;

    private transient final AuthenticationContext authenticationContext;

    private String clientId;

    private Optional<String> coopInfo;

    public CooperateController(CooperateInfoService cooperateInfoService, AuthenticationContext authenticationContext) {
        this.cooperateInfoService = cooperateInfoService;
        this.authenticationContext = authenticationContext;
    }

    @Override
    public String getPageTitle() {
        return null;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.clientId = parameter;
        this.coopInfo = event.getLocation().getQueryParameters().getSingleParameter("coopInfo");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        authenticationContext.getAuthenticatedUser(User.class).ifPresentOrElse(user -> {
            cooperateInfoService.createCooperateInfo(clientId, user.getUserInfo().getId(), coopInfo.orElse(""));
            event.forwardTo(CooperationView.class);
            Notification.show("Cooperation created!", 3000, Notification.Position.TOP_CENTER).addThemeName("success");
        }, () -> event.forwardTo(LoginView.class));
    }
}
