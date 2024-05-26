package ru.ancndz.timeapp.ui.view;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.timepicker.TimePickerVariant;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ancndz.timeapp.coop.CooperateInfoService;
import ru.ancndz.timeapp.coop.domain.CooperateInfo;
import ru.ancndz.timeapp.security.AuthorizationService;
import ru.ancndz.timeapp.timesheet.TimesheetService;
import ru.ancndz.timeapp.timesheet.WeekUtils;
import ru.ancndz.timeapp.timesheet.domain.TimesheetEntry;
import ru.ancndz.timeapp.timesheet.domain.WeekViewItem;
import ru.ancndz.timeapp.ui.IconViewContainer;
import ru.ancndz.timeapp.ui.component.DayCard;
import ru.ancndz.timeapp.user.domain.User;
import ru.ancndz.timeapp.user.domain.UserInfo;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Представление недельного расписания.
 *
 * @author Anton Utkaev
 * @since 2024.04.19
 */
@SpringComponent
@RouteScope
@Route(value = WeekView.LAYOUT_ROUTE, layout = MainLayout.class)
@PermitAll
public class WeekView extends Composite<VerticalLayout> implements IconViewContainer, HasDynamicTitle {

    private static final Logger log = LoggerFactory.getLogger(WeekView.class);

    public static final String LAYOUT_ROUTE = "week-view";

    public static final String LAYOUT_TITLE = "app.view-title.week-view";

    private static final String WORKER_TAB_ID = "WORKER_TAB_ID";

    private final TimesheetService timesheetService;

    private final CooperateInfoService cooperateInfoService;

    private final AuthorizationService authorizationService;

    private final transient AuthenticationContext authenticationContext;

    private final Map<DayOfWeek, DayCard> dayCardLayoutMap;

    private LocalDate currentStart;

    private Boolean workerTabSelected = false;

    /**
     * Конструктор.
     *
     * @param timesheetService
     *            сервис расписания
     * @param cooperateInfoService
     *            сервис сотрудничества
     * @param authorizationService
     *            сервис авторизации
     * @param authenticationContext
     *            контекст аутентификации
     */
    public WeekView(final TimesheetService timesheetService,
            final CooperateInfoService cooperateInfoService,
            final AuthorizationService authorizationService,
            final AuthenticationContext authenticationContext) {
        this.timesheetService = timesheetService;
        this.cooperateInfoService = cooperateInfoService;
        this.authorizationService = authorizationService;
        this.authenticationContext = authenticationContext;
        this.dayCardLayoutMap = new HashMap<>();
        this.currentStart = WeekUtils.getWeekStartDate(LocalDate.now());

        draw(authenticationContext.getAuthenticatedUser(User.class));
    }

    /**
     * Отрисовывает представление.
     *
     * @param currentUserDetailsOpt
     *            опциональные данные текущего пользователя
     */
    private void draw(final Optional<User> currentUserDetailsOpt) {
        getContent().setPadding(false);
        addClassNames(LumoUtility.Padding.Vertical.SMALL);
        getContent().getThemeList().add("spacing-xs");

        final DatePicker datePicker = getDatePicker();
        final HorizontalLayout weekNavigation = getWeekNavigation(datePicker);
        final Scroller scroller = getWeekScroller(datePicker);
        final VerticalLayout addingForm = getAddingForm(currentUserDetailsOpt);
        final Tabs tabs = getMainTabs(datePicker, addingForm, currentUserDetailsOpt);

        getContent().add(tabs, weekNavigation, scroller, addingForm);

        updateAllData(datePicker.getValue());
    }

    /**
     * Возвращает навигацию по неделям.
     *
     * @param datePicker
     *            компонент выбора даты
     * @return навигация по неделям
     */
    private HorizontalLayout getWeekNavigation(DatePicker datePicker) {
        final Button previousWeekButton = new Button(VaadinIcon.ARROW_LEFT.create());
        previousWeekButton.addClickListener(event -> {
            final LocalDate newDate = datePicker.getValue().minusWeeks(1);
            datePicker.setValue(newDate);
            currentStart = WeekUtils.getWeekStartDate(newDate);
            updateAllData(newDate);
        });
        previousWeekButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        final Button nextWeekButton = new Button(VaadinIcon.ARROW_RIGHT.create());
        nextWeekButton.addClickListener(event -> {
            final LocalDate newDate = datePicker.getValue().plusWeeks(1);
            datePicker.setValue(newDate);
            currentStart = WeekUtils.getWeekStartDate(newDate);
            updateAllData(newDate);
        });
        nextWeekButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        final HorizontalLayout weekNavigation = new HorizontalLayout(datePicker, previousWeekButton, nextWeekButton);
        weekNavigation.setAlignItems(FlexComponent.Alignment.CENTER);
        weekNavigation.setPadding(true);
        weekNavigation.addClassNames(LumoUtility.Display.INLINE_FLEX);
        weekNavigation.setWidthFull();
        weekNavigation.addClassNames(LumoUtility.Padding.Vertical.NONE);
        return weekNavigation;
    }

    /**
     * Возвращает скроллер с днями недели.
     *
     * @param datePicker
     *            компонент выбора даты
     * @return скроллер с днями недели
     */
    private Scroller getWeekScroller(final DatePicker datePicker) {
        final HorizontalLayout weekRow = new HorizontalLayout();
        weekRow.setId("weekRow");
        weekRow.setPadding(true);
        weekRow.addClassNames(LumoUtility.Display.INLINE_FLEX);

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            final DayCard dayCol = new DayCard(timesheetService);
            dayCol.setDayOnClick(datePicker);
            dayCardLayoutMap.put(dayOfWeek, dayCol);
            weekRow.add(dayCol);
        }

        final Scroller scroller = new Scroller();
        scroller.setContent(weekRow);
        scroller.setScrollDirection(Scroller.ScrollDirection.HORIZONTAL);
        scroller.setWidthFull();
        return scroller;
    }

    /**
     * Возвращает вкладки для основного представления.
     *
     * @param datePicker
     *            компонент выбора даты
     * @param addingForm
     *            форма добавления
     * @param currentUserDetails
     *            опциональные данные текущего пользователя
     * @return вкладки для основного представления
     */
    private Tabs getMainTabs(final DatePicker datePicker,
            final VerticalLayout addingForm,
            final Optional<User> currentUserDetails) {
        final Tabs tabs = new Tabs();
        if (currentUserDetails.map(authorizationService::isWorker).orElse(false)) {
            final Tab workerTab = new Tab(getTranslation("app.tab.worker"));
            workerTab.setId(WORKER_TAB_ID);
            tabs.add(workerTab);
            workerTabSelected = true;

            final Tab clientTab = new Tab(getTranslation("app.tab.client"));
            tabs.add(clientTab);
            tabs.setWidthFull();
            tabs.addSelectedChangeListener(event -> {
                workerTabSelected = event.getSelectedTab().getId().map(WORKER_TAB_ID::equals).orElse(false);
                addingForm.setVisible(workerTabSelected);
                updateAllData(datePicker.getValue());
            });
        } else {
            addingForm.setVisible(false);
            tabs.setVisible(false);
        }
        return tabs;
    }

    /**
     * Возвращает компонент выбора даты.
     *
     * @return компонент выбора даты
     */
    private DatePicker getDatePicker() {
        final DatePicker datePicker = new DatePicker(LocalDate.now(), getLocale());
        datePicker.setI18n(new DatePicker.DatePickerI18n().setFirstDayOfWeek(1));
        datePicker.addValueChangeListener(event -> {
            final LocalDate newWeekStartDate = WeekUtils.getWeekStartDate(event.getValue());
            if (currentStart.equals(newWeekStartDate)) {
                selectDayCard(event.getValue());
            } else {
                log.debug("Date from other week was selected ({}). Updating all data.", event.getValue());
                currentStart = newWeekStartDate;
                updateAllData(event.getValue());
            }
        });
        getContent().setAlignSelf(FlexComponent.Alignment.START, datePicker);
        return datePicker;
    }

    /**
     * Возвращает форму добавления.
     *
     * @param currentUserDetails
     *            опциональные данные текущего пользователя
     * @return форма добавления
     */
    private VerticalLayout getAddingForm(final Optional<User> currentUserDetails) {
        final Select<UserInfo> userSelect = new Select<>();
        userSelect.setPlaceholder(getTranslation("app.select.user-and-time-label"));
        currentUserDetails.ifPresent(details -> {
            userSelect.setDataProvider(
                    DataProvider.fromStream(cooperateInfoService.getWorkerCooperateInfos(details.getUserInfo())
                            .stream()
                            .map(CooperateInfo::getClient)));
        });
        userSelect.setItemLabelGenerator(UserInfo::getName);
        userSelect.addThemeVariants(SelectVariant.LUMO_SMALL);

        final TimePicker timePicker = new TimePicker();
        timePicker.setStep(Duration.ofMinutes(30));
        timePicker.addThemeVariants(TimePickerVariant.LUMO_SMALL);

        final FormLayout formLayout = new FormLayout(userSelect, timePicker);
        formLayout.setHeight("min-content");

        final VerticalLayout addingForm =
                new VerticalLayout(formLayout, getAddEntryButton(userSelect, timePicker, currentUserDetails));
        addingForm.setHeight("min-content");
        addingForm.setSpacing(false);
        addingForm.setWidth("300px");
        addingForm.addClassNames(LumoUtility.Border.ALL,
                LumoUtility.BorderRadius.SMALL,
                LumoUtility.BorderColor.CONTRAST_10,
                LumoUtility.AlignSelf.CENTER);
        return addingForm;
    }

    /**
     * Возвращает кнопку добавления записи.
     *
     * @param userSelect
     *            компонент выбора пользователя
     * @param timePicker
     *            компонент выбора времени
     * @param currentUserDetails
     *            опциональные данные текущего пользователя
     * @return кнопка добавления записи
     */
    private Button getAddEntryButton(final Select<UserInfo> userSelect,
            final TimePicker timePicker,
            final Optional<User> currentUserDetails) {
        final Button addEntryButton = new Button(getTranslation("app.button.add-new-timesheet-entry"));
        addEntryButton.setEnabled(false);
        addEntryButton.setDisableOnClick(true);
        addEntryButton.setWidth("min-content");
        addEntryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        addEntryButton.addClickListener(event -> {
            currentUserDetails.ifPresentOrElse(userDetails -> {
                final TimesheetEntry timesheetEntry = TimesheetEntry.newEntry()
                        .withWorker(userDetails.getUserInfo())
                        .withClient(userSelect.getValue())
                        .withEntryTime(timePicker.getValue())
                        .withEntryDate(getSelectedDayCard().getDate())
                        .build();
                timesheetService.save(timesheetEntry);
                updateSelectedDay();
            }, () -> log.warn("Anonymous user is trying to add new schedule entry"));
            timePicker.clear();
            event.getSource().setEnabled(false);
        });

        userSelect.addValueChangeListener(event -> {
            if (!timePicker.isEmpty()) {
                addEntryButton.setEnabled(true);
            }
        });
        timePicker.addValueChangeListener(event -> {
            if (!userSelect.isEmpty()) {
                addEntryButton.setEnabled(true);
            }
        });
        return addEntryButton;
    }

    /**
     * Обновляет выбранный день.
     */
    private void updateSelectedDay() {
        final DayCard selectedDay = getSelectedDayCard();
        final LocalDate selectedDayDate = selectedDay.getDate();
        final List<TimesheetEntry> schedulesOfDay;
        if (workerTabSelected) {
            schedulesOfDay = timesheetService.getWorkerEntriesOfDay(
                    authenticationContext.getAuthenticatedUser(User.class).get().getId(),
                    selectedDayDate);
        } else {
            schedulesOfDay = timesheetService.getClientEntriesOfDay(
                    authenticationContext.getAuthenticatedUser(User.class).get().getId(),
                    selectedDayDate);
        }
        selectedDay.updateData(workerTabSelected, selectedDayDate, selectedDayDate, schedulesOfDay);
    }

    /**
     * Обновляет все данные.
     *
     * @param actualPickedDate
     *            актуальная выбранная дата
     */
    public void updateAllData(final LocalDate actualPickedDate) {
        final WeekViewItem schedulesOfWeek;
        if (workerTabSelected) {
            schedulesOfWeek = timesheetService.getWorkerEntriesOfWeekAndConvert(
                    authenticationContext.getAuthenticatedUser(User.class).get().getId(),
                    actualPickedDate);
        } else {
            schedulesOfWeek = timesheetService.getClientEntriesOfWeekAndConvert(
                    authenticationContext.getAuthenticatedUser(User.class).get().getId(),
                    actualPickedDate);
        }
        schedulesOfWeek.getMap().forEach((date, scheduleEntries) -> {
            dayCardLayoutMap.get(date.getDayOfWeek())
                    .updateData(workerTabSelected, date, actualPickedDate, scheduleEntries);
        });
    }

    /**
     * Выбирает карточку дня.
     *
     * @param dayCard
     *            карточка дня
     */
    private void selectDayCard(final DayCard dayCard) {
        dayCardLayoutMap.values().forEach(card -> card.select(false));
        dayCard.select(true);
        log.debug("Day was selected: {}", dayCard.getDate());
    }

    /**
     * Выбирает карточку дня по дате.
     *
     * @param date
     *            дата
     */
    private void selectDayCard(final LocalDate date) {
        selectDayCard(dayCardLayoutMap.get(date.getDayOfWeek()));
    }

    /**
     * Возвращает выбранную карточку дня.
     *
     * @return выбранная карточка дня
     */
    private DayCard getSelectedDayCard() {
        return dayCardLayoutMap.values().stream().filter(DayCard::isSelected).findFirst().get();
    }

    @Override
    public Icon getIcon() {
        return VaadinIcon.CALENDAR.create();
    }

    @Override
    public String getPageTitle() {
        return getTranslation(LAYOUT_TITLE);
    }

}
