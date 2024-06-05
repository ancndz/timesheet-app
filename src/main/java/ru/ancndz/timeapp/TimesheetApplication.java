package ru.ancndz.timeapp;

import com.vaadin.flow.i18n.I18NProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.ancndz.timeapp.core.ApplicationInfo;
import ru.ancndz.timeapp.security.SecurityConfig;

import java.util.List;
import java.util.Locale;

/**
 * Главный класс приложения.
 *
 * @author Anton Utkaev
 * @since 2024.05.26
 */
@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
@Import(value = { H2Configuration.class, DevConfiguration.class, SecurityConfig.class })
public class TimesheetApplication extends SpringBootServletInitializer {

    public static final Logger log = LoggerFactory.getLogger(TimesheetApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TimesheetApplication.class, args);
    }

    @Bean
    public ApplicationRunner appInfo(final ApplicationInfo applicationInfo) {
        return args -> {
            log.info("Current version: {}:{}", applicationInfo.getVersionNumber(), applicationInfo.getBuildNumber());
        };
    }

    /**
     * Провайдер локализации.
     *
     * @param messageSource
     *            источник сообщений
     * @param locales
     *            список локалей
     * @return провайдер локализации
     */
    @Bean(name = "MessageSourceI18NProvider")
    @Profile("prod")
    public I18NProvider i18NProvider(final MessageSource messageSource, final List<Locale> locales) {
        return new I18NProvider() {

            @Override
            public List<Locale> getProvidedLocales() {
                return locales;
            }

            @Override
            public String getTranslation(String key, Locale locale, Object... params) {
                return messageSource.getMessage(key, params, locale);
            }
        };
    }

    /**
     * Список локалей.
     *
     * @return список локалей
     */
    @Bean(name = "locales")
    public List<Locale> locales() {
        return List.of(Locale.ENGLISH, Locale.forLanguageTag("ru"));
    }
}
