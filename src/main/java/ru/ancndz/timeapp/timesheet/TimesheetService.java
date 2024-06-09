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
     * Получить записи рабочего за выбранные дни.
     *
     * @param workerId
     *            идентификатор рабочего
     * @param days
     *            дни
     * @return список записей
     */
    List<TimesheetEntry> getWorkerEntriesOfDays(String workerId, List<LocalDate> days);

    /**
     * Получить записи клиента за выбранные дни.
     *
     * @param clientId
     *            идентификатор клиента
     * @param days
     *            даты
     * @return список записей
     */
    List<TimesheetEntry> getClientEntriesOfDays(String clientId, List<LocalDate> days);

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
     * Получить все записи клиента.
     *
     * @param clientId
     *            идентификатор клиента
     * @return список записей
     */
    List<TimesheetEntry> getAllClientEntries(String clientId);

    /**
     * Получить все записи рабочего.
     *
     * @param workerId
     *            идентификатор рабочего
     * @return список записей
     */
    List<TimesheetEntry> getAllWorkerEntries(String workerId);

    /**
     * Получить записи по клиенту и рабочему.
     *
     * @param clientId
     *            идентификатор клиента
     * @param workerId
     *            идентификатор рабочего
     * @return список записей
     */
    List<TimesheetEntry> getEntriesByClientAndWorker(String clientId, String workerId);

    /**
     * Сохранить запись.
     *
     * @param entry
     *            запись
     */
    void save(TimesheetEntry entry);
}
