package ru.ancndz.timeapp.ui.view;

import com.google.zxing.WriterException;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ancndz.timeapp.core.QrCodeGenerator;
import ru.ancndz.timeapp.ui.IconViewContainer;
import ru.ancndz.timeapp.ui.component.userform.CurrentUserFormComponent;
import ru.ancndz.timeapp.ui.controller.CooperateController;
import ru.ancndz.timeapp.user.UserService;
import ru.ancndz.timeapp.user.domain.User;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Представление профиля пользователя.
 *
 * @author Anton Utkaev
 * @since 2024.05.14
 */
@SpringComponent
@RouteScope
@Route(value = ProfileView.LAYOUT_ROUTE, layout = MainLayout.class)
@PermitAll
public class ProfileView extends Composite<VerticalLayout> implements HasDynamicTitle, IconViewContainer {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ProfileView.class);

    public static final String LAYOUT_ROUTE = "profile";

    public static final String LAYOUT_TITLE = "app.view-title.profile-view";

    public ProfileView(final UserService userService, final AuthenticationContext authenticationContext) {
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().add(new CurrentUserFormComponent(userService, authenticationContext));

        authenticationContext.getAuthenticatedUser(User.class).ifPresent(this::placeQrCode);
    }

    /**
     * Размещает QR-код для текущего пользователя.
     *
     * @param currentUser
     *            текущий пользователь
     */
    private void placeQrCode(final User currentUser) {
        final Image qrCode = createQrCode(currentUser);
        if (qrCode != null) {
            final Div qrCodeContainer = new Div(qrCode);
            qrCodeContainer.addClassNames(LumoUtility.AlignItems.CENTER);
            final Dialog qrCodeDialog = new Dialog(qrCodeContainer);
            qrCodeDialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
            qrCodeDialog.setHeaderTitle(getTranslation("app.dialog.qr-code"));
            qrCodeDialog.getHeader().add(new Button(VaadinIcon.CLOSE.create(), e -> qrCodeDialog.close()));
            qrCodeDialog.setCloseOnEsc(true);
            qrCodeDialog.setMaxWidth(qrCode.getWidth());

            final Div footer = new Div(qrCode.getText());
            footer.setMaxWidth(qrCode.getWidth());
            footer.addClassNames(LumoUtility.FontSize.XXSMALL,
                    LumoUtility.TextAlignment.LEFT,
                    LumoUtility.TextOverflow.ELLIPSIS,
                    LumoUtility.Whitespace.NOWRAP,
                    LumoUtility.TextColor.TERTIARY);
            qrCodeDialog.getFooter().add(footer);

            final Button qrCodeButton = new Button(getTranslation("app.button.show-qr-code"), e -> qrCodeDialog.open());
            getContent().add(qrCodeButton);
        }
    }

    /**
     * Создает QR-код для текущего пользователя.
     *
     * @param currentUser
     *            текущий пользователь
     * @return QR-код
     */
    @Nullable
    private Image createQrCode(final User currentUser) {
        try {
            final String url =
                    RouteConfiguration.forSessionScope().getUrl(CooperateController.class, currentUser.getId());
            final String fullUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(url).toUriString();
            log.debug("Created QR code URL: {}", url);
            byte[] qrCodeImage = QrCodeGenerator.generateQRCodeImage(fullUrl, 250, 250);
            final StreamResource resource =
                    new StreamResource("qrcode.png", () -> new ByteArrayInputStream(qrCodeImage));
            final Image qrCode = new Image(resource, url);
            qrCode.setText(fullUrl);
            qrCode.setWidth("250px");
            qrCode.setHeight("250px");

            return qrCode;
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getPageTitle() {
        return getTranslation(LAYOUT_TITLE);
    }

    @Override
    public Icon getIcon() {
        return VaadinIcon.USER_CARD.create();
    }
}
