package ru.ancndz.timeapp.timesheet;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;

/**
 * Утилиты для работы с неделями.
 *
 * @author Anton Utkaev
 * @since 2024.04.19
 */
public class WeekUtils {

    /**
     * Получить дату начала недели.
     *
     * @param date
     *            дата
     * @return дата начала недели
     */
    public static LocalDate getWeekStartDate(final LocalDate date) {
        final DayOfWeek firstDayOfWeek = WeekFields.ISO.getFirstDayOfWeek();
        return date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
    }

}
