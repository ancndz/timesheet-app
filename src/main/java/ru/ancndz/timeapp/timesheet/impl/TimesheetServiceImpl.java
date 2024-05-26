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
    public List<TimesheetEntry> getWorkerEntriesOfDay(final String workerId, final LocalDate date) {
        final QTimesheetEntry qTimesheetEntry = QTimesheetEntry.timesheetEntry;
        return StreamSupport
                .stream(timesheetEntryRepository
                        .findAll(qTimesheetEntry.worker.id.eq(workerId).and(qTimesheetEntry.entryDate.eq(date)))
                        .spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimesheetEntry> getWorkerEntriesOfWeek(final String workerId, final LocalDate date) {
        final QTimesheetEntry QTimesheetEntry = ru.ancndz.timeapp.timesheet.domain.QTimesheetEntry.timesheetEntry;
        final LocalDate startOfWeek = WeekUtils.getWeekStartDate(date);
        return StreamSupport.stream(timesheetEntryRepository.findAll(QTimesheetEntry.worker.id.eq(workerId)
                .and(QTimesheetEntry.entryDate.between(startOfWeek, startOfWeek.plusDays(6)))
                .and(QTimesheetEntry.archivedAt.isNull())).spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public WeekViewItem convertToViewItem(final LocalDate date, final List<TimesheetEntry> entries) {
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
    @Transactional(readOnly = true)
    public List<TimesheetEntry> getClientEntriesOfWeek(String clientId, LocalDate date) {
        final QTimesheetEntry qTimesheetEntry = QTimesheetEntry.timesheetEntry;
        final LocalDate startOfWeek = WeekUtils.getWeekStartDate(date);
        return StreamSupport.stream(timesheetEntryRepository.findAll(qTimesheetEntry.client.id.eq(clientId)
                .and(qTimesheetEntry.entryDate.between(startOfWeek, startOfWeek.plusWeeks(1)))
                .and(qTimesheetEntry.archivedAt.isNull())).spliterator(), false).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimesheetEntry> getClientEntriesOfDay(String clientId, LocalDate selectedDayDate) {
        final QTimesheetEntry qTimesheetEntry = QTimesheetEntry.timesheetEntry;
        return StreamSupport.stream(timesheetEntryRepository.findAll(qTimesheetEntry.client.id.eq(clientId)
                .and(qTimesheetEntry.entryDate.eq(selectedDayDate))
                .and(qTimesheetEntry.archivedAt.isNull())).spliterator(), false).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void save(TimesheetEntry entry) {
        storeService.store(new StoreContext(entry));
    }
}
