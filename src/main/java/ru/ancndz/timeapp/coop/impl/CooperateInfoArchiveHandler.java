package ru.ancndz.timeapp.coop.impl;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.ancndz.timeapp.coop.domain.CooperateInfo;
import ru.ancndz.timeapp.core.BeforeStoreEvent;
import ru.ancndz.timeapp.timesheet.TimesheetService;
import ru.ancndz.timeapp.timesheet.domain.TimesheetEntry;

import java.util.List;

/**
 * Обработчик архивирования информации о сотрудничестве.
 *
 * @author Anton Utkaev
 * @since 2024.06.10
 */
@Component
public class CooperateInfoArchiveHandler {

    private final TimesheetService timesheetService;

    public CooperateInfoArchiveHandler(TimesheetService timesheetService) {
        this.timesheetService = timesheetService;
    }

    /**
     * Обработка архивирования информации о сотрудничестве.
     *
     * @param event
     *            событие сохранения
     */
    @EventListener(BeforeStoreEvent.class)
    public void onCooperateInfoArchive(final BeforeStoreEvent event) {
        final List<CooperateInfo> coops = event.getStoreContext()
                .getObjects()
                .stream()
                .filter(CooperateInfo.class::isInstance)
                .map(CooperateInfo.class::cast)
                .filter(CooperateInfo::isDeleted)
                .toList();

        coops.forEach(cooperateInfo -> {
            final List<TimesheetEntry> entries = timesheetService
                    .getEntriesByClientAndWorker(cooperateInfo.getClient().getId(), cooperateInfo.getWorker().getId());
            entries.forEach(entry -> {
                entry.setDeleted(true);
                event.getStoreContext().add(entry);
            });
        });
    }
}
