package ru.ancndz.timeapp;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Конфигурация для H2.
 *
 * @author Anton Utkaev
 * @since 2024.04.22
 */
@Configuration
@Profile({ "postgres" })
public class H2Configuration {

}
