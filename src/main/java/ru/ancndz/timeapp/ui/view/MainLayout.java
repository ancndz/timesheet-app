package ru.ancndz.timeapp.ui.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import ru.ancndz.timeapp.ui.component.SideLinkNav;
import ru.ancndz.timeapp.ui.component.ThemeSwitchButton;
import ru.ancndz.timeapp.user.domain.User;

import java.util.Optional;

/**
 * Главный макет приложения.
 *
 * @author Anton Utkaev
 * @since 2024.04.16
 */
public class MainLayout extends AppLayout {

    public static final Logger log = LoggerFactory.getLogger(MainLayout.class);

    public MainLayout(final ApplicationContext applicationContext, final AuthenticationContext authenticationContext) {
        final Optional<User> currentScheduleUser = authenticationContext.getAuthenticatedUser(User.class);

        final H1 title = new H1(getTranslation("app.title"));
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextAlignment.LEFT, LumoUtility.Margin.Right.LARGE);
        title.setHeight("min-content");
        final Div filler = new Div();

        final HorizontalLayout headerRow =
                new HorizontalLayout(new DrawerToggle(), title, filler, new ThemeSwitchButton());
        headerRow.setId("header");
        headerRow.addClassNames(LumoUtility.Gap.SMALL, LumoUtility.Padding.Horizontal.MEDIUM);
        headerRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        headerRow.expand(filler);
        headerRow.setWidthFull();
        addToNavbar(headerRow);
        setPrimarySection(Section.DRAWER);

        final VerticalLayout sideLinkNavWrapper = new VerticalLayout();
        sideLinkNavWrapper.setSpacing(false);
        sideLinkNavWrapper.setPadding(false);
        final Scroller scroller = new Scroller(sideLinkNavWrapper);
        scroller.setClassName(LumoUtility.Padding.SMALL);
        addToDrawer(scroller);

        currentScheduleUser.ifPresentOrElse(user -> {
            final H2 welcomeText =
                    new H2(getTranslation("app.message.main-view.welcome", user.getUserInfo().getName()));
            welcomeText.addClassNames(LumoUtility.FontSize.MEDIUM,
                    LumoUtility.TextAlignment.LEFT,
                    LumoUtility.TextOverflow.ELLIPSIS);
            final Div welcomeTextWrapper = new Div(welcomeText);
            welcomeTextWrapper.addClassNames(LumoUtility.Padding.MEDIUM);

            final SideLinkNav sideLinkNav = new SideLinkNav(applicationContext, user.getAuthorities());
            final Button logoutButton =
                    new Button(getTranslation("app.button.logout"), e -> authenticationContext.logout());
            sideLinkNavWrapper.add(welcomeTextWrapper, logoutButton, new Hr(), sideLinkNav);
            sideLinkNavWrapper.setAlignSelf(FlexComponent.Alignment.END, logoutButton);
        }, () -> {
            final Button loginButton = new Button(getTranslation("app.button.login"),
                    e -> e.getSource().getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
            sideLinkNavWrapper.add(loginButton);
            sideLinkNavWrapper.setAlignSelf(FlexComponent.Alignment.END, loginButton);
        });

    }

}