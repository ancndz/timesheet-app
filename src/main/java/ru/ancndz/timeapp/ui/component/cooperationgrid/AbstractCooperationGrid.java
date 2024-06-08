package ru.ancndz.timeapp.ui.component.cooperationgrid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnRendering;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ancndz.timeapp.coop.CooperateInfoService;
import ru.ancndz.timeapp.coop.domain.CooperateInfo;
import ru.ancndz.timeapp.security.ValidationException;
import ru.ancndz.timeapp.ui.component.DeleteButton;
import ru.ancndz.timeapp.user.UserService;
import ru.ancndz.timeapp.user.domain.UserInfo;

import java.util.Comparator;

/**
 * Абстрактная таблица сотрудничества.
 *
 * @author Anton Utkaev
 * @since 2024.04.29
 */
public abstract class AbstractCooperationGrid extends Grid<CooperateInfo> {

    private static final Logger log = LoggerFactory.getLogger(AbstractCooperationGrid.class);

    protected static final String USER_NAME_COLUMN_KEY = "userNameColumnKey";

    protected final CooperateInfoService cooperateInfoService;

    protected transient final UserInfo currentUserInfo;

    protected AbstractCooperationGrid(final CooperateInfoService cooperateInfoService, final UserInfo currentUserInfo) {
        this.cooperateInfoService = cooperateInfoService;
        this.currentUserInfo = currentUserInfo;

        setColumnRendering(ColumnRendering.LAZY);
        addClassName("cooperation-grid");

        addColumn(createUserInfoRenderer(info -> info.getClient().getName(), info -> info.getClient().getEmail()))
                .setKey(USER_NAME_COLUMN_KEY)
                .setHeader(getTranslation("app.field.coop.client"))
                .setTooltipGenerator(info -> info.getClient().getName())
                .setAutoWidth(true)
                .setComparator(Comparator.comparing(info -> info.getClient().getName()))
                .setSortable(true);

        setDataProvider(DataProvider.ofCollection(cooperateInfoService.getWorkerCooperateInfos(currentUserInfo)));

        setPartNameGenerator(cooperateInfo -> {
            if (!cooperateInfo.isActive()) {
                return "non-active";
            }
            if (cooperateInfo.getArchivedAt() != null) {
                return "archived";
            }
            return null;
        });
    }

    /**
     * Создание рендерера информации о пользователе.
     *
     * @return рендерер
     */
    public static <T> Renderer<T> createUserInfoRenderer(ValueProvider<T, String> nameProvider,
            ValueProvider<T, String> emailProvider) {
        return LitRenderer.<T>of("<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                + "<vaadin-avatar name=\"${item.fullName}\" alt=\"User avatar\"></vaadin-avatar>"
                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                + "    <span> ${item.fullName} </span>"
                + "    <span style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">"
                + "      ${item.email}"
                + "    </span>"
                + "  </vaadin-vertical-layout>"
                + "</vaadin-horizontal-layout>")
                .withProperty("fullName", nameProvider)
                .withProperty("email", emailProvider);
    }

    /**
     * Получение кнопки добавления сотрудничества.
     *
     * @param userService
     *            сервис пользователей
     * @param currentUserInfo
     *            текущий пользователь
     * @return кнопка добавления сотрудничества
     */
    public Button getCooperationAddButton(final UserService userService, final UserInfo currentUserInfo) {
        return new Button(getTranslation("app.button.add-cooperation"), VaadinIcon.PLUS.create(), buttonClickEvent -> {
            final Dialog addDialog = new Dialog();
            addDialog.setCloseOnOutsideClick(true);
            addDialog.setCloseOnEsc(true);
            addDialog.setHeaderTitle(buttonClickEvent.getSource().getText());

            final TextField searchField = new TextField(getTranslation("app.coop.search"));
            final Select<UserInfo> userSelect = new Select<>();
            userSelect.setItemLabelGenerator(item -> item.getName() + " " + item.getPhoneNumber());

            searchField.addValueChangeListener(event -> {
                userSelect.setItems(userService.searchUsersByValue(searchField.getValue()));
            });

            final TextField infoField = new TextField(getTranslation("app.field.coop.info"));

            final FormLayout formLayout = new FormLayout(searchField, userSelect, infoField);

            final Button cancelButton =
                    new Button(getTranslation("app.button.cancel"), cancelEvent -> addDialog.close());
            final Button saveButton = new Button(getTranslation("app.button.save"), saveEvent -> {
                try {
                    final CooperateInfo newCoopInfo =
                            cooperateInfoService.createCooperateInfo(userSelect.getValue().getId(),
                                    currentUserInfo.getId(),
                                    infoField.getValue());
                    ((ListDataProvider) getDataProvider()).getItems().add(newCoopInfo);
                    getDataProvider().refreshAll();
                    addDialog.close();
                } catch (ValidationException e) {
                    Notification.show(String.join(", ", e.getErrors()), 3000, Notification.Position.BOTTOM_CENTER);
                }
            });

            addDialog.getFooter().add(cancelButton, saveButton);
            addDialog.add(formLayout);
            addDialog.open();
        });
    }

    /**
     * Создание кнопки удаления информации о сотрудничестве.
     *
     * @param info
     *            информация о сотрудничестве
     * @return кнопка удаления
     */
    protected Button createDeleteInfoButton(final CooperateInfo info) {
        final String text = getTranslation("app.button.delete-user", info.getClient().getName());
        return DeleteButton.newButton().withMainText(text).withConfirmListener(event -> {
            try {
                cooperateInfoService.deleteCooperateInfo(info);
                ((ListDataProvider) getDataProvider()).getItems().remove(info);
                getDataProvider().refreshAll();
            } catch (ValidationException e) {
                Notification.show(String.join(", ", e.getErrors()), 3000, Notification.Position.BOTTOM_CENTER);
            }
        }).closingParentDialog().build();
    }

}
