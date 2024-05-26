package ru.ancndz.timeapp.timesheet;

import ru.ancndz.timeapp.timesheet.domain.TimesheetEntry;
import ru.ancndz.timeapp.timesheet.domain.WeekViewItem;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для работы с записями табеля учета рабочего времени.
 *
 * @author Anton Utkaev
 * @since 2024.04.18
 */
public interface TimesheetService {

    /**
     * Получить записи рабочего за день.
     *
     * @param workerId
     *            идентификатор рабочего
     * @param date
     *            дата
     * @return список записей
     */
    List<TimesheetEntry> getWorkerEntriesOfDay(String workerId, LocalDate date);

    /**
     * Получить записи рабочего за неделю.
     *
     * @param workerId
     *            идентификатор рабочего
     * @param date
     *            дата
     * @return список записей
     */
    List<TimesheetEntry> getWorkerEntriesOfWeek(String workerId, LocalDate date);

    /**
     * Преобразовать записи рабочего за неделю в представление.
     *
     * @param date
     *            дата
     * @param entries
     *            список записей
     * @return представление
     */
    WeekViewItem convertToViewItem(LocalDate date, List<TimesheetEntry> entries);

    /**
     * Получить записи рабочего за неделю и преобразовать в представление.
     *
     * @param workerId
     *            идентификатор рабочего
     * @param date
     *            дата
     * @return представление
     */
    WeekViewItem getWorkerEntriesOfWeekAndConvert(String workerId, LocalDate date);

    /**
     * Получить записи клиента за неделю и преобразовать в представление.
     *
     * @param clientId
     *            идентификатор клиента
     * @param date
     *            дата
     * @return представление
     */
    WeekViewItem getClientEntriesOfWeekAndConvert(String clientId, LocalDate date);

    /**
     * Получить записи клиента за неделю.
     *
     * @param clientId
     *            идентификатор клиента
     * @param date
     *            дата
     * @return список записей
     */
    List<TimesheetEntry> getClientEntriesOfWeek(String clientId, LocalDate date);

    /**
     * Получить записи клиента за день.
     *
     * @param clientId
     *            идентификатор клиента
     * @param selectedDayDate
     *            дата
     * @return список записей
     */
    List<TimesheetEntry> getClientEntriesOfDay(String clientId, LocalDate selectedDayDate);

    /**
     * Сохранить запись.
     *
     * @param entry
     *            запись
     */
    void save(TimesheetEntry entry);
}
