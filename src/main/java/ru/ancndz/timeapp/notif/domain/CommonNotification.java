package ru.ancndz.timeapp.notif.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ru.ancndz.timeapp.core.domain.AbstractEntity;
import ru.ancndz.timeapp.user.domain.UserInfo;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Общее уведомление.
 *
 * @author Anton Utkaev
 * @since 2024.05.18
 */
@Entity
@Table(name = "timetable_notification")
@Inheritance
@DiscriminatorColumn(name = "dtype")
@DiscriminatorValue("base")
public class CommonNotification extends AbstractEntity {

    @Column
    private String message;

    @Column
    private NotificationType type;

    @Column
    private String status;

    @Column
    private LocalDateTime createdAt;

    @ManyToOne
    private UserInfo user;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime localDateTime) {
        this.createdAt = localDateTime;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
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
                && Objects.equals(user, that.user)
                && Objects.equals(sender, that.sender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, type, status, createdAt, user, sender);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CommonNotification.class.getSimpleName() + "[", "]")
                .add("message='" + message + "'")
                .add("type='" + type + "'")
                .add("status='" + status + "'")
                .add("localDateTime=" + createdAt)
                .add("user=" + user.getId())
                .add("user=" + sender.getId())
                .add("archivedAt=" + archivedAt)
                .add("id='" + getId() + "'")
                .add("deleted=" + isDeleted())
                .add("isNew=" + isNew())
                .toString();
    }

    public static CommonBuilder newNotification() {
        return new CommonNotification().new CommonBuilder();
    }

    public class CommonBuilder extends AbstractBuilder<CommonBuilder, CommonNotification> {

        protected String message;

        protected CommonBuilder() {
        }

        @Override
        protected CommonNotification createEmpty() {
            return new CommonNotification();
        }

        @Override
        public CommonNotification build() {
            final CommonNotification notification = super.build();
            notification.setType(NotificationType.COMMON);
            return notification;
        }

    }

    /**
     * Абстрактный билдер.
     *
     * @param <T>
     *            тип билдера
     * @param <N>
     *            тип уведомления
     */
    protected abstract class AbstractBuilder<T extends AbstractBuilder, N extends CommonNotification> {

        UserInfo user;

        UserInfo sender;

        LocalDateTime createdAt;

        String message;

        protected AbstractBuilder() {
        }

        protected T self() {
            return (T) this;
        }

        protected abstract N createEmpty();

        public T withUser(UserInfo user) {
            this.user = user;
            return self();
        }

        public T withSender(UserInfo sender) {
            this.sender = sender;
            return self();
        }

        public T withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return self();
        }

        public T withMessage(String message) {
            this.message = message;
            return self();
        }

        public N build() {
            final N notification = createEmpty();
            notification.setId(UUID.randomUUID().toString());
            notification.setNew(true);
            notification.setUser(this.user);
            notification.setSender(this.sender);
            if (notification.getCreatedAt() == null) {
                notification.setCreatedAt(LocalDateTime.now());
            }
            notification.setMessage(this.message);
            return notification;
        }
    }
}
