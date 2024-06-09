package ru.ancndz.timeapp;

import com.vaadin.flow.i18n.I18NProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ancndz.timeapp.coop.domain.CooperateInfo;
import ru.ancndz.timeapp.core.StoreContext;
import ru.ancndz.timeapp.core.StoreService;
import ru.ancndz.timeapp.notif.domain.CommonNotification;
import ru.ancndz.timeapp.notif.domain.NotificationType;
import ru.ancndz.timeapp.notif.domain.NotificationTypeSystemName;
import ru.ancndz.timeapp.notif.domain.repo.NotificationTypeRepository;
import ru.ancndz.timeapp.security.Role;
import ru.ancndz.timeapp.security.UserRoleService;
import ru.ancndz.timeapp.timesheet.WeekUtils;
import ru.ancndz.timeapp.timesheet.domain.TimesheetEntry;
import ru.ancndz.timeapp.user.domain.User;
import ru.ancndz.timeapp.user.domain.UserInfo;
import ru.ancndz.timeapp.user.domain.repo.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Конфигурация для профиля разработки.
 *
 * @author Anton Utkaev
 * @since 2024.04.22
 */
@Configuration
@Profile("dev")
public class DevConfiguration {

    private final static Logger log = LoggerFactory.getLogger(DevConfiguration.class);

    /**
     * Провайдер локализации.
     *
     * @param messageSource
     *            источник сообщений
     * @param locales
     *            список локалей
     * @return провайдер локализации
     */
    @Bean(name = "MessageSourceI18NProviderDev")
    public I18NProvider i18NProvider(final MessageSource messageSource, final List<Locale> locales) {
        return new I18NProvider() {

            @Override
            public List<Locale> getProvidedLocales() {
                return locales;
            }

            @Override
            public String getTranslation(String key, Locale locale, Object... params) {
                String message;
                try {
                    message = messageSource.getMessage(key, params, locale);
                } catch (NoSuchMessageException e) {
                    log.warn("No message for such locale! Key: {}, locale: {}", key, locale);
                    message = key;
                }
                return message;
            }
        };
    }

    /**
     * Стартовые данные.
     *
     * @param storeService
     *            сервис хранилища
     * @param passwordEncoder
     *            кодировщик паролей
     * @param userRepository
     *            репозиторий пользователей
     * @return командная строка
     */
    @Bean
    @Order(2)
    public CommandLineRunner dataLoader(final StoreService storeService,
            final PasswordEncoder passwordEncoder,
            final NotificationTypeRepository notificationTypeRepository,
            final UserRepository userRepository) {
        return args -> {
            if (userRepository.count() > 2) {
                log.info("Data already loaded");
                return;
            }
            log.info("Loading data...");
            final StoreContext storeContext = new StoreContext();
            final String defaultPassword = passwordEncoder.encode("1");

            final User admin1 = User.newUser()
                    .withUsername("admin")
                    .withPassword(defaultPassword)
                    .withAuthorities(UserRoleService.ROLE_GRANTED_AUTHORITY_MAP.get(Role.ADMIN))
                    .withUserInfo(UserInfo.newUserInfo().withName("testAdmin").withEmail("admin@admin.com").build())
                    .build();
            storeContext.add(admin1);
            final User admin2 = User.newUser()
                    .withUsername("admin2")
                    .withPassword(defaultPassword)
                    .withAuthorities(UserRoleService.ROLE_GRANTED_AUTHORITY_MAP.get(Role.ADMIN))
                    .withUserInfo(UserInfo.newUserInfo().withName("testAdmin2").withEmail("admin2@admin.com").build())
                    .build();
            storeContext.add(admin2);

            final User worker = User.newUser()
                    .withUsername("worker")
                    .withPassword(defaultPassword)
                    .withAuthorities(UserRoleService.ROLE_GRANTED_AUTHORITY_MAP.get(Role.WORKER),
                            UserRoleService.ROLE_GRANTED_AUTHORITY_MAP.get(Role.USER))
                    .withUserInfo(UserInfo.newUserInfo()
                            .withName("workerName")
                            .withPhoneNumber("+79999999999")
                            .withEmail("worker@mail.mail")
                            .build())
                    .build();
            storeContext.add(worker);

            createUsers(worker, defaultPassword, storeContext);

            createNotifications(admin1, worker, notificationTypeRepository, storeContext);

            storeService.store(storeContext);
        };
    }

    private void createNotifications(User admin1,
            User worker,
            NotificationTypeRepository notificationTypeRepository,
            StoreContext storeContext) {
        final NotificationType commonType =
                notificationTypeRepository.findBySystemName(NotificationTypeSystemName.COMMON);
        for (int i = 1; i <= 5; i++) {
            final CommonNotification notification = CommonNotification.newNotification()
                    .withType(commonType)
                    .withAddressee(worker.getUserInfo())
                    .withSender(admin1.getUserInfo())
                    .withCreatedAt(LocalDate.now().atTime(10, 0))
                    .withMessage("Test message #" + i)
                    .build();
            storeContext.add(notification);
        }
    }

    private void createUsers(User worker, String defaultPassword, StoreContext storeContext) {
        for (int i = 1; i <= 20; i++) {
            final User user = User.newUser()
                    .withUsername("user" + i)
                    .withPassword(defaultPassword)
                    .withAuthorities(UserRoleService.ROLE_GRANTED_AUTHORITY_MAP.get(Role.USER))
                    .withUserInfo(UserInfo.newUserInfo()
                            .withName("userName" + i)
                            .withEmail("testEmail" + i + "@mail.mail")
                            .withPhoneNumber("+700000000" + String.format("%02d", i))
                            .build())
                    .build();
            storeContext.add(user);

            final CooperateInfo cooperateInfo = CooperateInfo.newCooperateInfo()
                    .withWorker(worker.getUserInfo())
                    .withClient(user.getUserInfo())
                    .withInfo("cooperation #" + i)
                    .build();
            cooperateInfo.setActive(i > 5);
            storeContext.add(cooperateInfo);

            final LocalDate startOfWeek = WeekUtils.getWeekStartDate(LocalDate.now());
            final int entriesRandom = new Random().nextInt(2) + 1;
            for (int j = 0; j < entriesRandom; j++) {
                final TimesheetEntry timesheetEntry = TimesheetEntry.newEntry()
                        .withWorker(worker.getUserInfo())
                        .withClient(user.getUserInfo())
                        .withEntryDate(getRandomDate(startOfWeek, startOfWeek.plusWeeks(1)))
                        .withEntryTime(getRandomTime())
                        .build();
                storeContext.add(timesheetEntry);
            }
        }

    }

    private LocalDate getRandomDate(LocalDate minDate, LocalDate maxDate) {
        long minDay = minDate.toEpochDay();
        long maxDay = maxDate.toEpochDay();
        long randomDay = minDay + new Random().nextInt((int) (maxDay - minDay));
        return LocalDate.ofEpochDay(randomDay);
    }

    private LocalTime getRandomTime() {
        return LocalTime.of(ThreadLocalRandom.current().nextInt(8, 20), 0);
    }

}
