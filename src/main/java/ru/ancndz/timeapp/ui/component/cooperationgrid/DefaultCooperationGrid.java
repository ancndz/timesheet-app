package ru.ancndz.timeapp.ui.component.cooperationgrid;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.theme.lumo.LumoUtility;
import ru.ancndz.timeapp.coop.CooperateInfoService;
import ru.ancndz.timeapp.coop.domain.CooperateInfo;
import ru.ancndz.timeapp.user.PhoneNumberConverter;
import ru.ancndz.timeapp.user.domain.UserInfo;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;

/**
 * Таблица сотрудничества по умолчанию.
 *
 * @author Anton Utkaev
 * @since 2024.04.29
 */
public class DefaultCooperationGrid extends AbstractCooperationGrid {

    /**
     * Создание таблицы сотрудничества по умолчанию.
     *
     * @param cooperateInfoService
     *            сервис сотрудничества
     * @param currentUserInfo
     *            информация о текущем пользователе
     */
    protected DefaultCooperationGrid(CooperateInfoService cooperateInfoService, final UserInfo currentUserInfo) {
        super(cooperateInfoService, currentUserInfo);
        setMaxWidth("80%");

        getColumnByKey(AbstractCooperationGrid.USER_NAME_COLUMN_KEY).setFlexGrow(0);
        addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        addComponentColumn(info -> new Anchor("tel:" + info.getClient().getPhoneNumber(),
                PhoneNumberConverter.convertPhoneNumber(info.getClient().getPhoneNumber()))).setAutoWidth(true)
                .setFlexGrow(0)
                .setHeader(getTranslation("app.field.user-info.phone-number"));

        addColumn(new LocalDateRenderer<>(CooperateInfo::getCoopDate,
                () -> DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
                .setHeader(getTranslation("app.field.coop.coop-date"))
                .setAutoWidth(true)
                .setFlexGrow(0);

        final Grid.Column<CooperateInfo> infoColumn = addColumn(CooperateInfo::getInfo).setKey("infoColumn")
                .setHeader(getTranslation("app.field.coop.info"))
                .setAutoWidth(true);

        createEditColumn(infoColumn);
        createDeleteButton();
    }

    /**
     * Создание кнопки удаления информации о сотрудничестве.
     *
     * @param infoColumn
     *            колонка с информацией о сотрудничестве
     */
    private void createEditColumn(final Grid.Column<CooperateInfo> infoColumn) {
        final Editor<CooperateInfo> editor = getEditor();
        editor.setBuffered(true);

        final TextField infoField = new TextField();
        infoField.setWidthFull();
        infoColumn.setEditorComponent(infoField);

        final Binder<CooperateInfo> binder = new Binder<>(CooperateInfo.class);
        binder.bind(infoField, CooperateInfo::getInfo, CooperateInfo::setInfo);
        editor.setBinder(binder);

        final Button saveButton = new Button(VaadinIcon.ENTER_ARROW.create(), e -> editor.save());
        saveButton.addFocusShortcut(Key.ENTER);
        final Button cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);

        final HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
        actions.setPadding(false);
        actions.setSpacing(false);
        actions.addClassNames(LumoUtility.Display.INLINE_FLEX, LumoUtility.Gap.XSMALL);

        final Grid.Column<CooperateInfo> editColumn =
                addComponentColumn(info -> new Button(VaadinIcon.EDIT.create(), e -> {
                    if (editor.isOpen()) {
                        editor.cancel();
                    }
                    editor.editItem(info);
                })).setWidth("min-content").setFlexGrow(0).setKey("editButtonColumn");
        editColumn.setEditorComponent(actions);

        editor.addSaveListener(event -> cooperateInfoService.save(event.getItem()));
    }

    /**
     * Создание кнопки удаления информации о сотрудничестве.
     */
    private void createDeleteButton() {
        addComponentColumn(this::createDeleteInfoButton).setWidth("min-content").setFlexGrow(0);
    }
}
