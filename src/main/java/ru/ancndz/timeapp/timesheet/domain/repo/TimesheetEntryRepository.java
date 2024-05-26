package ru.ancndz.timeapp.timesheet.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.ancndz.timeapp.timesheet.domain.TimesheetEntry;

/**
 * Репозиторий для записей табеля учета рабочего времени.
 *
 * @author Anton Utkaev
 * @since 2024.04.18
 */
@Repository
public interface TimesheetEntryRepository
        extends JpaRepository<TimesheetEntry, String>, QuerydslPredicateExecutor<TimesheetEntry> {
}
