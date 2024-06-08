package ru.ancndz.timeapp.ui.component;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ancndz.timeapp.timesheet.TimesheetService;
import ru.ancndz.timeapp.timesheet.domain.TimesheetEntry;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Карточка дня.
 *
 * @author Anton Utkaev
 * @since 2024.04.19
 */
public class DayCard extends Composite<VerticalLayout> {

    private static final Logger log = LoggerFactory.getLogger(DayCard.class);

    private static final String SELECTED_COLOR = LumoUtility.BorderColor.PRIMARY_50;

    private static final String TODAY_COLOR = LumoUtility.BorderColor.ERROR_50;

    private static final String DEFAULT_COLOR = LumoUtility.BorderColor.CONTRAST_20;

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    private final H3 dayOfWeekNameText;

    private LocalDate cardDate;

    private final H3 cardDateText;

    private final Grid<TimesheetEntry> scheduleEntries;

    private boolean selected;

    public DayCard(final TimesheetService timesheetService) {

        getContent().setHeightFull();
        getContent().setMinWidth("300px");
        getContent().setMinHeight("300px");
        getContent().setMaxHeight("350px");
        getContent().setFlexGrow(1);
        getContent().setSpacing(false);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        addClassNames(LumoUtility.Border.ALL,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderColor.CONTRAST_10);
        setVisible(false);

        cardDateText = new H3();

        dayOfWeekNameText = new H3();
        dayOfWeekNameText.setWidth("max-content");

        scheduleEntries = new Grid<>();
        scheduleEntries.addClassName("day-card-grid");
        scheduleEntries.setId("scheduleEntries");
        scheduleEntries.addClassNames(LumoUtility.Border.NONE, LumoUtility.Background.TRANSPARENT);
        scheduleEntries.getStyle().set("--vaadin-grid-cell-background", "var(--lumo-contrast-5pct)");
        scheduleEntries.getStyle().set("--vaadin-grid-cell-padding", "var(--lumo-space-wide-s)");
        scheduleEntries.setPartNameGenerator(scheduleEntry -> LocalDateTime.now()
                .isBefore(LocalDateTime.of(scheduleEntry.getEntryDate(), scheduleEntry.getEntryTime()))
                        ? "future"
                        : "past");

        createNameColumn(scheduleEntries);
        createEditColumn(scheduleEntries, timesheetService);

        final Hr hr = new Hr();
        hr.addClassNames(LumoUtility.Margin.Vertical.SMALL);

        getContent().add(cardDateText, dayOfWeekNameText, hr, scheduleEntries);
    }

    /**
     * Создать колонку с именем.
     *
     * @param scheduleEntries
     *            таблица
     */
    private void createNameColumn(final Grid<TimesheetEntry> scheduleEntries) {
        // todo: получение имени работника при данных для клиента
        scheduleEntries
                .addColumn(scheduleEntry -> MessageFormat.format("{0}: {1}",
                        scheduleEntry.getEntryTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
                        scheduleEntry.getClient().getName()))
                .setKey("entryTime")
                .setTooltipGenerator(scheduleEntry -> scheduleEntry.getClient().getName())
                .setComparator(Comparator.comparing(TimesheetEntry::getEntryTime))
                .setFlexGrow(1);
    }

    /**
     * Создать колонку с кнопками редактирования.
     *
     * @param entryGrid
     *            таблица
     * @param timesheetService
     *            сервис
     */
    private void createEditColumn(final Grid<TimesheetEntry> entryGrid, final TimesheetService timesheetService) {
        entryGrid.addComponentColumn(entry -> {
            final Button editButton = createEditButton(entryGrid, entry, timesheetService);
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

            final Button button = getDeleteButton(entryGrid, entry, timesheetService);
            button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);

            final HorizontalLayout actions = new HorizontalLayout(editButton, button);
            actions.setPadding(false);
            actions.setSpacing(false);
            actions.addClassNames(LumoUtility.Display.INLINE_FLEX, LumoUtility.Gap.XSMALL);

            return actions;
        }).setKey("editColumnKey").setWidth("min-content").setFlexGrow(0);
    }

    /**
     * Создать кнопку удаления.
     *
     * @param entryGrid
     *            таблица
     * @param entry
     *            запись
     * @param timesheetService
     *            сервис
     * @return кнопка
     */
    private Button getDeleteButton(final Grid<TimesheetEntry> entryGrid,
            final TimesheetEntry entry,
            final TimesheetService timesheetService) {
        return DeleteButton.newButton()
                .withMainText(
                        MessageFormat.format(getTranslation("app.button.delete-user"), entry.getClient().getName()))
                .withConfirmListener(event -> {
                    entry.setDeleted(true);
                    timesheetService.save(entry);
                    ((ListDataProvider) entryGrid.getDataProvider()).getItems().remove(entry);
                    entryGrid.getDataProvider().refreshAll();
                })
                .build();
    }

    /**
     * Создать кнопку редактирования.
     *
     * @param entryGrid
     *            таблица
     * @param entry
     *            запись
     * @param timesheetService
     *            сервис
     * @return кнопка
     */
    private Button createEditButton(final Grid<TimesheetEntry> entryGrid,
            final TimesheetEntry entry,
            final TimesheetService timesheetService) {
        return new Button(VaadinIcon.EDIT.create(), event -> {
            final Dialog dialog = new Dialog();
            dialog.setCloseOnOutsideClick(true);
            dialog.setCloseOnEsc(true);

            final FormLayout formLayout = new FormLayout();
            final TextField nameField =
                    new TextField(getTranslation("app.field.user-info.name.alt"), entry.getClient().getName(), "");
            nameField.setReadOnly(true);
            final TimePicker timePicker = new TimePicker(getTranslation("app.field.timesheet-entry.time"));
            final DatePicker datePicker = new DatePicker(getTranslation("app.field.timesheet-entry.date"));

            final Binder<TimesheetEntry> binder = new Binder<>(TimesheetEntry.class);
            binder.forField(timePicker)
                    .asRequired(getTranslation("app.error.time-required"))
                    .bind(TimesheetEntry::getEntryTime, TimesheetEntry::setEntryTime);
            binder.forField(datePicker)
                    .asRequired(getTranslation("app.error.date-required"))
                    .bind(TimesheetEntry::getEntryDate, TimesheetEntry::setEntryDate);
            binder.readBean(entry);

            final Button cancelButton = new Button(getTranslation("app.button.cancel"), cancelEvent -> dialog.close());
            final Button saveButton = new Button(getTranslation("app.button.cancel"), saveEvent -> {
                if (binder.writeBeanIfValid(entry)) {
                    timesheetService.save(entry);
                    entryGrid.getDataProvider().refreshItem(entry);
                    entryGrid.sort(List
                            .of(new GridSortOrder<>(entryGrid.getColumnByKey("entryTime"), SortDirection.ASCENDING)));
                }
                dialog.close();
            });
            formLayout.add(nameField, timePicker, datePicker);
            dialog.add(formLayout);
            dialog.getFooter().add(cancelButton, saveButton);
            dialog.open();
        });
    }

    /**
     * Обновить данные.
     *
     * @param workerTabSelected
     *            выбрана вкладка работника
     * @param date
     *            дата
     * @param actualDatePicked
     *            выбранная дата
     * @param data
     *            данные
     */
    public void updateData(final boolean workerTabSelected,
            final LocalDate date,
            final LocalDate actualDatePicked,
            final List<TimesheetEntry> data) {
        if (this.cardDate != null) {
            log.debug("Updating data: OLD date: [{}], NEW date: [{}]", cardDate, date);
        }
        this.cardDate = date;
        select(Objects.equals(actualDatePicked, date));
        cardDateText.setText(date.format(DATE_FORMATTER));
        dayOfWeekNameText.setText(date.getDayOfWeek().getDisplayName(TextStyle.FULL, getLocale()));

        setId(date.getDayOfWeek().toString());

        data.sort(Comparator.naturalOrder());
        scheduleEntries.setItems(data);
        scheduleEntries.getColumnByKey("editColumnKey").setVisible(workerTabSelected);

        setVisible(true);
    }

    /**
     * Изменить цвет.
     *
     * @param date
     *            дата
     */
    private void changeColor(final LocalDate date) {
        removeClassNames(SELECTED_COLOR, TODAY_COLOR, DEFAULT_COLOR);
        if (isSelected()) {
            addClassName(SELECTED_COLOR);
        } else if (LocalDate.now().equals(date)) {
            addClassName(TODAY_COLOR);
        } else {
            addClassName(DEFAULT_COLOR);
        }
    }

    /**
     * Выбрана ли карточка.
     *
     * @return выбрана ли карточка
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Получить дату.
     *
     * @return дата
     */
    public LocalDate getDate() {
        return cardDate;
    }

    /**
     * Выбрать карточку.
     *
     * @param selected
     *            выбрана ли карточка
     */
    public void select(boolean selected) {
        this.selected = selected;
        changeColor(cardDate);
    }

    /**
     * Установить обработчик клика по дню.
     *
     * @param component
     *            компонент
     */
    public void setDayOnClick(final HasValue<?, LocalDate> component) {
        getContent().addClickListener(event -> component.setValue(cardDate));
    }
}
