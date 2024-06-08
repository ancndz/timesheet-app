package ru.ancndz.timeapp.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnRendering;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.GrantedAuthority;
import ru.ancndz.timeapp.core.domain.AbstractEntity;
import ru.ancndz.timeapp.security.Role;
import ru.ancndz.timeapp.security.UserRoleService;
import ru.ancndz.timeapp.ui.IconViewContainer;
import ru.ancndz.timeapp.ui.component.cooperationgrid.AbstractCooperationGrid;
import ru.ancndz.timeapp.user.PhoneNumberConverter;
import ru.ancndz.timeapp.user.UserService;
import ru.ancndz.timeapp.user.domain.User;

import java.text.MessageFormat;
import java.util.Comparator;

/**
 * Представление информации о приложении для администратора.
 *
 * @author Anton Utkaev
 * @since 2024.05.01
 */
@SpringComponent
@RouteScope
@Route(value = UsersView.LAYOUT_ROUTE, layout = MainLayout.class)
@RolesAllowed(Role.ADMIN)
@RoutePrefix("system")
public class UsersView extends Composite<VerticalLayout> implements HasDynamicTitle, IconViewContainer {

    public static final String LAYOUT_ROUTE = "users";

    public static final String LAYOUT_TITLE = "app.view-title.users";

    public UsersView(final UserService userService,
            final AuthenticationContext authenticationContext,
            final UserRoleService userRoleService) {

        final Grid<User> userGrid = new Grid<>();
        userGrid.addClassName("users-grid");
        userGrid.setColumnRendering(ColumnRendering.LAZY);

        userGrid.setPartNameGenerator(user -> {
            if (!user.isAccountNonLocked()) {
                return "blocked-user";
            }
            return null;
        });

        final String currentUserId = authenticationContext.getAuthenticatedUser(User.class).get().getId();
        userGrid.setClassNameGenerator(user -> currentUserId.equals(user.getId()) ? "disabled" : "enabled");

        final DataProvider<User,
                Void> dataProvider = new CallbackDataProvider<>(
                        query -> userService.findAll(VaadinSpringDataHelpers.toSpringPageRequest(query)),
                        query -> Math.toIntExact(userService.countAll()),
                        AbstractEntity::getId);

        userGrid.setDataProvider(dataProvider);

        userGrid.addColumn(AbstractCooperationGrid.createUserInfoRenderer(user -> user.getUserInfo().getName(),
                user -> user.getUserInfo().getEmail()))
                .setHeader(getTranslation("app.field.coop.client"))
                .setTooltipGenerator(
                        user -> MessageFormat.format("{0} ({1})", user.getUserInfo().getName(), user.getUsername()))
                .setAutoWidth(true)
                .setFlexGrow(2)
                .setComparator(Comparator.comparing(user -> user.getUserInfo().getName()))
                .setSortable(true);

        userGrid.addComponentColumn(user -> new Anchor("tel:" + user.getUserInfo().getPhoneNumber(),
                PhoneNumberConverter.convertPhoneNumber(user.getUserInfo().getPhoneNumber())))
                .setHeader(getTranslation("app.field.user-info.phone-number"))
                .setAutoWidth(true)
                .setFlexGrow(1);

        userGrid.addColumn(User::getAuthorities)
                .setHeader(getTranslation("app.field.user.authorities"))
                .setFlexGrow(1)
                .setAutoWidth(true);

        userGrid.addComponentColumn(user -> getActions(user, userService, userGrid)).setAutoWidth(true).setFlexGrow(1);

        getContent().add(userGrid);
    }

    private Component getActions(final User pickedUser, UserService userService, final Grid<User> userGrid) {
        return new Button(getTranslation("app.button.edit"), event -> {
            final Binder<User> userBinder = new Binder<>(User.class);
            userBinder.setBean(pickedUser);
            final Checkbox lockedCheckbox = new Checkbox(getTranslation("app.field.user.is-account-non-locked"));
            lockedCheckbox.addClassNames(LumoUtility.Margin.Top.MEDIUM);
            userBinder.forField(lockedCheckbox).bind(User::isAccountNonLocked, User::setAccountNonLocked);

            final CheckboxGroup<GrantedAuthority> rolesList =
                    new CheckboxGroup<>(getTranslation("app.field.user.authorities"));
            rolesList.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
            rolesList.setItems(UserRoleService.ROLE_GRANTED_AUTHORITY_MAP.values());
            rolesList.select(pickedUser.getAuthorities());
            rolesList.setItemLabelGenerator(GrantedAuthority::getAuthority);
            userBinder.forField(rolesList).bind(User::getAuthorities, User::setAuthorities);

            final FormLayout formLayout = new FormLayout();
            formLayout.add(rolesList, lockedCheckbox);
            formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

            final Dialog userInfoDialog = new Dialog();
            userInfoDialog.setCloseOnOutsideClick(true);
            userInfoDialog.setCloseOnEsc(true);
            userInfoDialog.setHeaderTitle(event.getSource().getText());
            userInfoDialog.add(formLayout);

            final Button saveButton = new Button(getTranslation("app.button.save"), e -> {
                userBinder.writeBeanIfValid(pickedUser);
                userService.saveUser(pickedUser);
                userGrid.getDataProvider().refreshItem(pickedUser);
                userInfoDialog.close();
            });
            saveButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            final Button cancelButton = new Button(getTranslation("app.button.cancel"), e -> userInfoDialog.close());
            userInfoDialog.getFooter().add(cancelButton, saveButton);

            userInfoDialog.open();

        });
    }

    @Override
    public Icon getIcon() {
        return VaadinIcon.USERS.create();
    }

    @Override
    public String getPageTitle() {
        return getTranslation(LAYOUT_TITLE);
    }
}
