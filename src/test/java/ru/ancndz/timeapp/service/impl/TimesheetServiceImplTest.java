package ru.ancndz.timeapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ancndz.timeapp.AbstractTest;
import ru.ancndz.timeapp.coop.domain.CooperateInfo;
import ru.ancndz.timeapp.coop.domain.repo.CooperateInfoRepository;
import ru.ancndz.timeapp.notif.domain.repo.CommonNotificationRepository;
import ru.ancndz.timeapp.timesheet.TimesheetService;
import ru.ancndz.timeapp.timesheet.domain.TimesheetEntry;
import ru.ancndz.timeapp.timesheet.domain.WeekViewItem;
import ru.ancndz.timeapp.timesheet.domain.repo.TimesheetEntryRepository;
import ru.ancndz.timeapp.user.domain.UserInfo;
import ru.ancndz.timeapp.user.domain.repo.UserInfoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Тесты для {@link TimesheetServiceImpl}.
 *
 * @author Anton Utkaev
 * @since 2024.04.18
 */
class TimesheetServiceImplTest extends AbstractTest {

    @Autowired
    private TimesheetService timesheetService;

    @Autowired
    private TimesheetEntryRepository timesheetEntryRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private CooperateInfoRepository cooperateInfoRepository;

    @Autowired
    private CommonNotificationRepository commonNotificationRepository;

    private String worker1Id;

    private String worker2Id;

    @BeforeEach
    void setUp() {

        final UserInfo worker1 = UserInfo.newUserInfo().withName("testWorker1").build();
        worker1Id = worker1.getId();
        final UserInfo worker2 = UserInfo.newUserInfo().withName("testWorker2").build();
        worker2Id = worker2.getId();

        final UserInfo userInfo1 = UserInfo.newUserInfo().withName("testUser1").build();
        final UserInfo userInfo2 = UserInfo.newUserInfo().withName("testUser1").build();

        final CooperateInfo cooperateInfo11 =
                CooperateInfo.newCooperateInfo().withClient(userInfo1).withWorker(worker1).build();

        final CooperateInfo cooperateInfo12 =
                CooperateInfo.newCooperateInfo().withClient(userInfo2).withWorker(worker1).build();

        final List<TimesheetEntry> scheduleEntries = new ArrayList<>();

        createScheduleEntry(worker1,
                userInfo1,
                scheduleEntries,
                LocalDate.of(2024, Month.APRIL, 15),
                LocalTime.of(12, 23, 23));

        createScheduleEntry(worker1,
                userInfo1,
                scheduleEntries,
                LocalDate.of(2024, Month.APRIL, 16),
                LocalTime.of(12, 23, 23));

        createScheduleEntry(worker1,
                userInfo1,
                scheduleEntries,
                LocalDate.of(2024, Month.APRIL, 17),
                LocalTime.of(12, 23, 23));

        createScheduleEntry(worker1,
                userInfo1,
                scheduleEntries,
                LocalDate.of(2024, Month.APRIL, 18),
                LocalTime.of(12, 23, 23));

        createScheduleEntry(worker1,
                userInfo1,
                scheduleEntries,
                LocalDate.of(2024, Month.APRIL, 19),
                LocalTime.of(12, 23, 23));

        createScheduleEntry(worker1,
                userInfo2,
                scheduleEntries,
                LocalDate.of(2024, Month.APRIL, 19),
                LocalTime.of(13, 23, 23));

        createScheduleEntry(worker1,
                userInfo1,
                scheduleEntries,
                LocalDate.of(2024, Month.APRIL, 16),
                LocalTime.of(12, 23, 23));

        createScheduleEntry(worker1,
                userInfo1,
                scheduleEntries,
                LocalDate.of(2024, Month.APRIL, 11),
                LocalTime.of(12, 23, 23));

        doInTransaction(false, () -> {
            userInfoRepository.saveAll(Arrays.asList(userInfo1, userInfo2, worker1, worker2));
            cooperateInfoRepository.saveAll(Arrays.asList(cooperateInfo11, cooperateInfo12));
            timesheetEntryRepository.saveAll(scheduleEntries);
        });
    }

    private void createScheduleEntry(UserInfo worker,
            UserInfo userInfo,
            List<TimesheetEntry> scheduleEntries,
            LocalDate entryDate,
            LocalTime entryTime) {
        final TimesheetEntry timesheetEntry = TimesheetEntry.newEntry()
                .withWorker(worker)
                .withClient(userInfo)
                .withEntryDate(entryDate)
                .withEntryTime(entryTime)
                .build();

        scheduleEntries.add(timesheetEntry);
    }

    @AfterEach
    void tearDown() {
        doInTransaction(false, () -> {
            timesheetEntryRepository.deleteAll();
            commonNotificationRepository.deleteAll();
            cooperateInfoRepository.deleteAll();
            userInfoRepository.deleteAll();
        });
        worker1Id = null;
        worker2Id = null;
    }

    /**
     * Проверка получения записей табеля рабочего за день.
     */
    @Test
    void getSchedulesOfDay() {
        final List<TimesheetEntry> scheduleEntries =
                timesheetService.getWorkerEntriesOfDay(worker1Id, LocalDate.of(2024, Month.APRIL, 19));
        assertThat(scheduleEntries).hasSize(2);

        final List<TimesheetEntry> emptyList =
                timesheetService.getWorkerEntriesOfDay(worker2Id, LocalDate.of(2024, Month.APRIL, 19));
        assertThat(emptyList).isEmpty();
    }

    /**
     * Проверка получения записей табеля рабочего за неделю.
     */
    @Test
    void getSchedulesOfWeek() {
        final List<TimesheetEntry> scheduleEntries =
                timesheetService.getWorkerEntriesOfWeek(worker1Id, LocalDate.of(2024, Month.APRIL, 19));
        assertThat(scheduleEntries).hasSize(7);

        final List<TimesheetEntry> lastWeekEntries =
                timesheetService.getWorkerEntriesOfWeek(worker1Id, LocalDate.of(2024, Month.APRIL, 9));
        assertThat(lastWeekEntries).hasSize(1);
    }

    /**
     * Проверка преобразования записей табеля рабочего за неделю в представление.
     */
    @Test
    void convertToViewItem() {
        final List<TimesheetEntry> scheduleEntries =
                timesheetService.getWorkerEntriesOfWeek(worker1Id, LocalDate.of(2024, Month.APRIL, 19));
        assertThat(scheduleEntries).hasSize(7);

        final WeekViewItem weekViewItem =
                timesheetService.convertToViewItem(LocalDate.of(2024, Month.APRIL, 19), scheduleEntries);

        assertThat(weekViewItem).satisfies(item -> {
            assertThat(item.getDayList(LocalDate.of(2024, Month.APRIL, 15))).hasSize(1);
            assertThat(item.getDayList(LocalDate.of(2024, Month.APRIL, 16))).hasSize(2);
            assertThat(item.getDayList(LocalDate.of(2024, Month.APRIL, 17))).hasSize(1);
            assertThat(item.getDayList(LocalDate.of(2024, Month.APRIL, 18))).hasSize(1);
            assertThat(item.getDayList(LocalDate.of(2024, Month.APRIL, 19))).hasSize(2);
            assertThat(item.getDayList(LocalDate.of(2024, Month.APRIL, 20))).hasSize(0);
            assertThat(item.getDayList(LocalDate.of(2024, Month.APRIL, 21))).hasSize(0);
        });
    }
}