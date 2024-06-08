package ru.ancndz.timeapp.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteData;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.GrantedAuthority;
import ru.ancndz.timeapp.security.UserRoleService;
import ru.ancndz.timeapp.ui.IconViewContainer;
import ru.ancndz.timeapp.ui.view.MainLayout;
import ru.ancndz.timeapp.ui.view.RegisterView;

import java.util.Collection;
import java.util.List;

/**
 * Сайдбар навигации по ссылкам.
 *
 * @author Anton Utkaev
 * @since 2024.05.07
 */
public class SideLinkNav extends Composite<SideNav> {

    private final static Logger log = LoggerFactory.getLogger(SideLinkNav.class);

    public SideLinkNav() {

    }

    public SideLinkNav(final ApplicationContext applicationContext, Collection<? extends GrantedAuthority> userRoles) {
        draw(applicationContext, userRoles);
    }

    public SideLinkNav(final ApplicationContext applicationContext,
            Collection<? extends GrantedAuthority> userRoles,
            final Class<? extends Component> parentClass) {
        draw(applicationContext, userRoles, parentClass);
    }

    public void draw(final ApplicationContext applicationContext, Collection<? extends GrantedAuthority> userRoles) {
        draw(applicationContext, userRoles, MainLayout.class);
    }

    /**
     * Отрисовка сайдбара.
     *
     * @param applicationContext
     *            контекст приложения
     * @param userRoles
     *            роли пользователя
     * @param parentClass
     *            класс родительского компонента
     */
    public void draw(final ApplicationContext applicationContext,
            Collection<? extends GrantedAuthority> userRoles,
            final Class<? extends Component> parentClass) {
        getContent().setWidthFull();
        RouteConfiguration.forSessionScope()
                .getAvailableRoutes()
                .stream()
                .filter(routeData -> parentClass.equals((routeData.getParentLayout())))
                .filter(this::isExcluded)
                .filter(routeData -> hasViewRole(userRoles, routeData))
                .forEach(routeData -> {
                    final Object bean = applicationContext.getBean(routeData.getNavigationTarget());
                    final String pageTitle = bean instanceof HasDynamicTitle
                            ? ((HasDynamicTitle) bean).getPageTitle()
                            : routeData.getNavigationTarget().getSimpleName();
                    final Icon icon = bean instanceof IconViewContainer ? ((IconViewContainer) bean).getIcon() : null;

                    final SideNavItem sideNavItem = new SideNavItem(pageTitle, routeData.getNavigationTarget(), icon);
                    Tooltip.forComponent(sideNavItem).withText(pageTitle);

                    getContent().addItem(sideNavItem);
                    log.debug("Found route for parent class {}: {}",
                            parentClass.getSimpleName(),
                            sideNavItem.getPath());
                });
    }

    /**
     * Проверка на исключение.
     *
     * @param routeData
     *            данные маршрута
     * @return результат проверки
     */
    private boolean isExcluded(RouteData routeData) {
        return routeData.getNavigationTarget() != RegisterView.class;
    }

    /**
     * Проверка на наличие роли просмотра.
     *
     * @param userRoles
     *            роли пользователя
     * @param routeData
     *            данные маршрута
     * @return результат проверки
     */
    private boolean hasViewRole(Collection<? extends GrantedAuthority> userRoles, RouteData routeData) {
        if (routeData.getNavigationTarget().isAnnotationPresent(RolesAllowed.class)) {
            var annotation = routeData.getNavigationTarget().getAnnotation(RolesAllowed.class);
            for (var role : annotation.value()) {
                if (userRoles.stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority()
                                .equals(UserRoleService.ROLE_PREFIX + role))) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Получение списка элементов.
     *
     * @return список элементов
     */
    public List<SideNavItem> getItems() {
        return getContent().getItems();
    }

}
