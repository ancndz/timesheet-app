package ru.ancndz.timeapp.timesheet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ru.ancndz.timeapp.core.domain.AbstractEntity;
import ru.ancndz.timeapp.user.domain.UserInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Запись табеля учета рабочего времени.
 *
 * @author Anton Utkaev
 * @since 2024.04.16
 */
@Entity
@Table(name = "timetable_schedule")
public class TimesheetEntry extends AbstractEntity implements Comparable<TimesheetEntry> {

    @ManyToOne
    private UserInfo worker;

    @ManyToOne
    private UserInfo client;

    @Column
    private LocalDate entryDate;

    @Column
    private LocalTime entryTime;

    @Column
    private LocalDateTime archivedAt;

    public TimesheetEntry() {
    }

    @Override
    public boolean isDeleted() {
        return archivedAt != null;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.archivedAt = deleted ? LocalDateTime.now() : null;
    }

    public UserInfo getWorker() {
        return worker;
    }

    public void setWorker(UserInfo worker) {
        this.worker = worker;
    }

    public UserInfo getClient() {
        return client;
    }

    public void setClient(UserInfo client) {
        this.client = client;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public LocalTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalTime entryTime) {
        this.entryTime = entryTime;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TimesheetEntry.class.getSimpleName() + "[", "]").add("id='" + getId() + "'")
                .add("worker=" + worker.getId())
                .add("client=" + client.getId())
                .add("entryDate=" + entryDate)
                .add("entryTime=" + entryTime)
                .toString();
    }

    @Override
    public int compareTo(TimesheetEntry o) {
        return LocalDateTime.of(entryDate, entryTime).compareTo(LocalDateTime.of(o.entryDate, o.entryTime));
    }

    public static Builder newEntry() {
        return new Builder();
    }

    public static class Builder {

        private UserInfo worker;

        private UserInfo client;

        private LocalDate entryDate;

        private LocalTime entryTime;

        private Builder() {
        }

        public Builder withWorker(UserInfo worker) {
            this.worker = worker;
            return this;
        }

        public Builder withClient(UserInfo client) {
            this.client = client;
            return this;
        }

        public Builder withEntryDate(LocalDate entryDate) {
            this.entryDate = entryDate;
            return this;
        }

        public Builder withEntryTime(LocalTime entryTime) {
            this.entryTime = entryTime;
            return this;
        }

        public TimesheetEntry build() {
            final TimesheetEntry timesheetEntry = new TimesheetEntry();
            timesheetEntry.setId(UUID.randomUUID().toString());
            timesheetEntry.setNew(true);
            timesheetEntry.setWorker(this.worker);
            timesheetEntry.setClient(this.client);
            timesheetEntry.setEntryDate(this.entryDate);
            timesheetEntry.setEntryTime(this.entryTime);
            return timesheetEntry;
        }
    }

}
