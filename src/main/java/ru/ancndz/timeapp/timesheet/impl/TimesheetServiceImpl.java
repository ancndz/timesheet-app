package ru.ancndz.timeapp.timesheet.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ancndz.timeapp.core.StoreContext;
import ru.ancndz.timeapp.core.StoreService;
import ru.ancndz.timeapp.timesheet.TimesheetService;
import ru.ancndz.timeapp.timesheet.WeekUtils;
import ru.ancndz.timeapp.timesheet.domain.QTimesheetEntry;
import ru.ancndz.timeapp.timesheet.domain.TimesheetEntry;
import ru.ancndz.timeapp.timesheet.domain.WeekViewItem;
import ru.ancndz.timeapp.timesheet.domain.repo.TimesheetEntryRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Реализация сервиса для работы с записями табеля учета рабочего времени.
 *
 * @author Anton Utkaev
 * @since 2024.04.18
 */
@Service
public class TimesheetServiceImpl implements TimesheetService {

    private final TimesheetEntryRepository timesheetEntryRepository;

    private final StoreService storeService;

    public TimesheetServiceImpl(final TimesheetEntryRepository timesheetEntryRepository,
            final StoreService storeService) {
        this.timesheetEntryRepository = timesheetEntryRepository;
        this.storeService = storeService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimesheetEntry> getWorkerEntriesOfDays(final String workerId, final List<LocalDate> days) {
        final QTimesheetEntry qTimesheetEntry = QTimesheetEntry.timesheetEntry;
        return StreamSupport
                .stream(timesheetEntryRepository
                        .findAll(qTimesheetEntry.worker.id.eq(workerId).and(qTimesheetEntry.entryDate.in(days)))
                        .spliterator(), false)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimesheetEntry> getClientEntriesOfDays(String clientId, List<LocalDate> days) {
        final QTimesheetEntry qTimesheetEntry = QTimesheetEntry.timesheetEntry;
        return StreamSupport.stream(timesheetEntryRepository.findAll(qTimesheetEntry.client.id.eq(clientId)
                .and(qTimesheetEntry.entryDate.in(days))
                .and(qTimesheetEntry.archivedAt.isNull())).spliterator(), false).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimesheetEntry> getWorkerEntriesOfWeek(final String workerId, final LocalDate date) {
        return getWorkerEntriesOfDays(workerId, getDatesInWeek(date));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimesheetEntry> getClientEntriesOfWeek(String clientId, LocalDate date) {
        return getClientEntriesOfDays(clientId, getDatesInWeek(date));
    }

    @Override
    @Transactional(readOnly = true)
    public WeekViewItem getWorkerEntriesOfWeekAndConvert(final String workerId, final LocalDate date) {
        return convertToViewItem(date, getWorkerEntriesOfWeek(workerId, date));
    }

    @Override
    @Transactional(readOnly = true)
    public WeekViewItem getClientEntriesOfWeekAndConvert(String clientId, LocalDate date) {
        return convertToViewItem(date, getClientEntriesOfWeek(clientId, date));
    }

    @Override
    public List<TimesheetEntry> getAllClientEntries(String clientId) {
        final QTimesheetEntry qTimesheetEntry = QTimesheetEntry.timesheetEntry;
        return StreamSupport
                .stream(timesheetEntryRepository.findAll(qTimesheetEntry.client.id.eq(clientId)).spliterator(), false)
                .toList();
    }

    @Override
    public List<TimesheetEntry> getAllWorkerEntries(String workerId) {
        final QTimesheetEntry qTimesheetEntry = QTimesheetEntry.timesheetEntry;
        return StreamSupport
                .stream(timesheetEntryRepository.findAll(qTimesheetEntry.worker.id.eq(workerId)).spliterator(), false)
                .toList();
    }

    @Override
    public List<TimesheetEntry> getEntriesByClientAndWorker(String clientId, String workerId) {
        final QTimesheetEntry qTimesheetEntry = QTimesheetEntry.timesheetEntry;
        return StreamSupport
                .stream(timesheetEntryRepository
                        .findAll(qTimesheetEntry.client.id.eq(clientId).and(qTimesheetEntry.worker.id.eq(workerId)))
                        .spliterator(), false)
                .toList();
    }

    /**
     * Получить даты в неделе.
     *
     * @param date
     *            дата
     * @return список дат
     */
    private List<LocalDate> getDatesInWeek(LocalDate date) {
        final LocalDate startOfWeek = WeekUtils.getWeekStartDate(date);
        return Stream.iterate(startOfWeek, dt -> dt.plusDays(1)).limit(7).toList();
    }

    /**
     * Преобразовать записи в представление.
     *
     * @param date
     *            дата
     * @param entries
     *            записи
     * @return представление
     */
    private WeekViewItem convertToViewItem(final LocalDate date, final List<TimesheetEntry> entries) {
        final WeekViewItem weekViewItem = new WeekViewItem();

        final LocalDate startDayOfWeek = WeekUtils.getWeekStartDate(date);
        final var scheduleMap = entries.stream().collect(Collectors.groupingBy(TimesheetEntry::getEntryDate));

        Stream.iterate(startDayOfWeek, dt -> dt.plusDays(1))
                .limit(7)
                .forEach(eachWeekDay -> weekViewItem.getMap()
                        .put(eachWeekDay, scheduleMap.getOrDefault(eachWeekDay, Collections.emptyList())));
        return weekViewItem;
    }

    @Override
    @Transactional
    public void save(TimesheetEntry entry) {
        storeService.store(new StoreContext(entry));
    }
}
