package ru.ancndz.timeapp.ui.view;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.ApplicationContext;
import ru.ancndz.timeapp.ui.IconViewContainer;
import ru.ancndz.timeapp.ui.component.SideLinkNav;
import ru.ancndz.timeapp.user.domain.User;

/**
 * Представление информации о приложении.
 *
 * @author Anton Utkaev
 * @since 2024.04.21
 */
@SpringComponent
@RouteScope
@Route(value = "", layout = MainLayout.class, absolute = true)
@ParentLayout(MainLayout.class)
@RoutePrefix(AppInfoView.LAYOUT_ROUTE)
@PermitAll
public class AppInfoView extends Composite<VerticalLayout> implements RouterLayout, HasDynamicTitle, IconViewContainer {

    public static final String LAYOUT_ROUTE = "app-info";

    public static final String LAYOUT_TITLE = "app.view-title.app-info-view";

    public AppInfoView(final ApplicationContext applicationContext, final AuthenticationContext authenticationContext) {
        authenticationContext.getAuthenticatedUser(User.class)
                .ifPresent(user -> getContent()
                        .add(new SideLinkNav(applicationContext, user.getAuthorities(), AppInfoView.class)));
        getContent().add(new Html(getTranslation("app.info")));
    }

    @Override
    public Icon getIcon() {
        return VaadinIcon.INFO.create();
    }

    @Override
    public String getPageTitle() {
        return getTranslation(LAYOUT_TITLE);
    }
}
