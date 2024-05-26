package ru.ancndz.timeapp.ui.view;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ancndz.timeapp.coop.CooperateInfoService;
import ru.ancndz.timeapp.ui.IconViewContainer;
import ru.ancndz.timeapp.ui.component.cooperationgrid.AbstractCooperationGrid;
import ru.ancndz.timeapp.ui.component.cooperationgrid.CooperationGridFactory;
import ru.ancndz.timeapp.user.UserService;
import ru.ancndz.timeapp.user.domain.Role;
import ru.ancndz.timeapp.user.domain.User;

/**
 * Представление сотрудничества.
 *
 * @author Anton Utkaev
 * @since 2024.04.22
 */
@SpringComponent
@RouteScope
@Route(value = CooperationView.LAYOUT_ROUTE, layout = MainLayout.class)
@RolesAllowed(Role.WORKER)
public class CooperationView extends Composite<VerticalLayout> implements IconViewContainer, HasDynamicTitle {

    private static final Logger log = LoggerFactory.getLogger(CooperationView.class);

    public static final String LAYOUT_ROUTE = "coop";

    public static final String LAYOUT_TITLE = "app.view-title.coop-view";

    public CooperationView(final CooperateInfoService cooperateInfoService,
            final UserService userService,
            final AuthenticationContext authenticationContext) {

        final User currentScheduleUser = authenticationContext.getAuthenticatedUser(User.class)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        final AbstractCooperationGrid grid = CooperationGridFactory
                .createGrid(cooperateInfoService, currentScheduleUser.getUserInfo(), resolveIfMobile());
        final Button button = grid.getCooperationAddButton(userService, currentScheduleUser.getUserInfo());
        getContent().add(new VerticalLayout(grid, button));
    }

    /**
     * Определяет, является ли текущее устройство мобильным.
     *
     * @return {@code true}, если устройство мобильное, иначе {@code false}
     */
    private boolean resolveIfMobile() {
        final WebBrowser browser = VaadinSession.getCurrent().getBrowser();
        return browser.isIPhone() || browser.isAndroid() || browser.isWindowsPhone();
    }

    @Override
    public Icon getIcon() {
        return VaadinIcon.USER.create();
    }

    @Override
    public String getPageTitle() {
        return getTranslation(LAYOUT_TITLE);
    }

}
