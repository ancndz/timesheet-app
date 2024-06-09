package ru.ancndz.timeapp.ui.component.cooperationgrid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.theme.lumo.LumoUtility;
import ru.ancndz.timeapp.coop.CooperateInfoService;
import ru.ancndz.timeapp.coop.domain.CooperateInfo;
import ru.ancndz.timeapp.user.PhoneNumberConverter;
import ru.ancndz.timeapp.user.domain.UserInfo;

/**
 * Таблица сотрудничества для мобильных устройств.
 *
 * @author Anton Utkaev
 * @since 2024.04.29
 */
public class MobileCooperationGrid extends AbstractCooperationGrid {

    /**
     * Создание таблицы сотрудничества для мобильных устройств.
     *
     * @param cooperateInfoService
     *            сервис сотрудничества
     * @param currentUserInfo
     *            информация о текущем пользователе
     */
    protected MobileCooperationGrid(CooperateInfoService cooperateInfoService, final UserInfo currentUserInfo) {
        super(cooperateInfoService, currentUserInfo);
        addItemClickListener(this::createUserInfoDialogWindow);
    }

    /**
     * Создание диалогового окна с информацией о пользователе.
     *
     * @param event
     *            событие клика по элементу таблицы
     */
    private void createUserInfoDialogWindow(final ItemClickEvent<CooperateInfo> event) {
        final Dialog userInfoDialog = new Dialog();
        userInfoDialog.setCloseOnOutsideClick(true);
        userInfoDialog.setCloseOnEsc(true);
        userInfoDialog.setHeaderTitle(getTranslation("app.dialog.user-info"));

        final CooperateInfo eventItem = event.getItem();
        final UserInfo client = eventItem.getClient();

        final TextField firstName = new TextField(getTranslation("app.field.user-info.name.alt"), client.getName(), "");
        firstName.setReadOnly(true);

        final Anchor phone = new Anchor("tel:" + client.getPhoneNumber(),
                PhoneNumberConverter.convertPhoneNumber(client.getPhoneNumber()));
        phone.addClassName(LumoUtility.Margin.SMALL);

        final EmailField email = new EmailField(getTranslation("app.field.user-info.email"));
        email.setValue(client.getEmail());
        email.setReadOnly(true);
        final DatePicker coopDate = new DatePicker(getTranslation("app.field.coop.coop-date"), eventItem.getCoopDate());
        coopDate.setLocale(getLocale());
        coopDate.setReadOnly(true);
        final TextField info = new TextField(getTranslation("app.field.coop.info"));

        final FormLayout formLayout = new FormLayout();
        formLayout.add(firstName, phone, email, coopDate, info);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("500px", 2));
        formLayout.setColspan(info, 2);
        formLayout.addClassNames(LumoUtility.Padding.XSMALL);

        userInfoDialog.add(formLayout);

        final Binder<CooperateInfo> binder = new Binder<>(CooperateInfo.class);
        binder.bind(info, CooperateInfo::getInfo, CooperateInfo::setInfo);
        binder.readBean(eventItem);

        final Button saveButton = new Button(getTranslation("app.button.save"), e -> {
            binder.writeBeanIfValid(eventItem);
            cooperateInfoService.save(eventItem);
            event.getSource().getDataProvider().refreshItem(eventItem);
            userInfoDialog.close();
        });
        final Button deleteButton = createDeleteInfoButton(eventItem);
        final Button cancelButton = new Button(getTranslation("app.button.cancel"), e -> userInfoDialog.close());
        userInfoDialog.getFooter().add(deleteButton, cancelButton, saveButton);

        userInfoDialog.open();
    }

}
