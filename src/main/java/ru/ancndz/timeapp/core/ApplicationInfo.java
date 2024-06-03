package ru.ancndz.timeapp.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Класс получения информации о приложении.
 *
 * @author Anton Utkaev
 * @since 2024.06.03
 */
@Component
public class ApplicationInfo {

    private final String appName;

    private final String appGroup;

    private final String versionNumber;

    private final String buildNumber;

    public ApplicationInfo(@Value("${spring.application.name}") String appName,
            @Value("${spring.application.group}") String appGroup,
            @Value("${app.version}") String versionNumber,
            @Value("${app.version.build}") String buildNumber) {
        this.appName = appName;
        this.appGroup = appGroup;
        this.versionNumber = versionNumber;
        this.buildNumber = buildNumber;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppGroup() {
        return appGroup;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public String getBuildNumber() {
        return buildNumber;
    }
}
