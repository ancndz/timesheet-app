package ru.ancndz.timeapp.timesheet.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

/**
 * Представление недели в виде карты дней и списков записей табеля.
 *
 * @author Anton Utkaev
 * @since 2024.04.18
 */
public class WeekViewItem implements Serializable {

    @JsonProperty
    private Map<LocalDate, List<TimesheetEntry>> map;

    public WeekViewItem() {
        map = new TreeMap<>(Comparator.naturalOrder());
    }

    @JsonCreator
    public WeekViewItem(Map<LocalDate, List<TimesheetEntry>> map) {
        this.map = map;
    }

    public List<TimesheetEntry> getDayList(final LocalDate date) {
        return map.get(date);
    }

    public Map<LocalDate, List<TimesheetEntry>> getMap() {
        return map;
    }

    public void setMap(Map<LocalDate, List<TimesheetEntry>> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", WeekViewItem.class.getSimpleName() + "[", "]").add("map=" + map).toString();
    }
}
