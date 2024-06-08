package ru.ancndz.timeapp.notif.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import ru.ancndz.timeapp.core.domain.AbstractEntity;
import ru.ancndz.timeapp.user.domain.UserInfo;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Модель уведомления.
 *
 * @author Anton Utkaev
 * @since 2024.05.18
 */
@Entity
@Table(name = "timetable_notification")
public class CommonNotification extends AbstractEntity {

    @Column
    private String message;

    @ManyToOne(optional = false)
    private NotificationType type;

    @Column
    private NotificationStatus status;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne
    private UserInfo addressee;

    @ManyToOne
    private UserInfo sender;

    @Column
    private LocalDateTime archivedAt;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime localDateTime) {
        this.createdAt = localDateTime;
    }

    public UserInfo getAddressee() {
        return addressee;
    }

    public void setAddressee(UserInfo user) {
        this.addressee = user;
    }

    public UserInfo getSender() {
        return sender;
    }

    public void setSender(UserInfo sender) {
        this.sender = sender;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CommonNotification that = (CommonNotification) o;
        return Objects.equals(message, that.message)
                && Objects.equals(type, that.type)
                && Objects.equals(status, that.status)
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(addressee, that.addressee)
                && Objects.equals(sender, that.sender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, type, status, createdAt, addressee, sender);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CommonNotification.class.getSimpleName() + "[", "]")
                .add("message='" + message + "'")
                .add("type='" + type + "'")
                .add("status='" + status + "'")
                .add("localDateTime=" + createdAt)
                .add("addressee=" + addressee.getId())
                .add("sender=" + (sender == null ? "null" : sender.getId()))
                .add("archivedAt=" + archivedAt)
                .add("id='" + getId() + "'")
                .add("deleted=" + isDeleted())
                .add("isNew=" + isNew())
                .toString();
    }

    public static CommonBuilder newNotification() {
        return new CommonBuilder();
    }

    public static class CommonBuilder {

        UserInfo addressee;

        UserInfo sender;

        LocalDateTime createdAt;

        String message;

        NotificationType notificationType;

        protected CommonBuilder() {
        }

        public CommonBuilder withAddressee(UserInfo user) {
            this.addressee = user;
            return this;
        }

        public CommonBuilder withSender(UserInfo sender) {
            this.sender = sender;
            return this;
        }

        public CommonBuilder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CommonBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public CommonBuilder withType(NotificationType notificationType) {
            this.notificationType = notificationType;
            return this;
        }

        public CommonNotification build() {
            final CommonNotification notification = new CommonNotification();
            notification.setId(UUID.randomUUID().toString());
            notification.setNew(true);
            notification.setType(Objects.requireNonNull(this.notificationType));
            notification.setAddressee(Objects.requireNonNull(this.addressee));
            notification.setSender(this.sender);
            notification.setStatus(NotificationStatus.CREATED);
            notification.setMessage(this.message);
            notification.setCreatedAt(Objects.requireNonNullElseGet(this.createdAt, LocalDateTime::now));
            return notification;
        }

    }

}
