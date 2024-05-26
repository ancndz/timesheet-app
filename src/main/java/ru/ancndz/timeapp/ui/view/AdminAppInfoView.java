package ru.ancndz.timeapp.ui.view;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.context.annotation.Profile;
import ru.ancndz.timeapp.user.domain.Role;

/**
 * Представление информации о приложении для администратора.
 *
 * @author Anton Utkaev
 * @since 2024.05.01
 */
@SpringComponent
@RouteScope
@Route(value = AdminAppInfoView.LAYOUT_ROUTE, layout = AppInfoView.class)
@Profile("dev")
@RolesAllowed(Role.ADMIN)
public class AdminAppInfoView extends Composite<VerticalLayout> implements HasDynamicTitle {

    public static final String LAYOUT_ROUTE = "admin";

    public AdminAppInfoView() {
        getContent().add(LAYOUT_ROUTE);
    }

    @Override
    public String getPageTitle() {
        return LAYOUT_ROUTE;
    }
}
