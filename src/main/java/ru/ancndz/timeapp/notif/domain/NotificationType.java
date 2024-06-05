package ru.ancndz.timeapp.notif.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import ru.ancndz.timeapp.core.domain.AbstractEntity;

/**
 * Тип уведомления.
 *
 * @author Anton Utkaev
 * @since 2024.05.19
 */
@Entity
@Table(name = "timetable_notification_type")
public class NotificationType extends AbstractEntity {

    private String name;

    private String systemName;

    private String message;

    public NotificationType() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
}
