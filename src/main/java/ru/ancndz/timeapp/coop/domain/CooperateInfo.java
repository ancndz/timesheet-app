package ru.ancndz.timeapp.coop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.ancndz.timeapp.core.domain.DomainEntity;
import ru.ancndz.timeapp.core.domain.SoftDeletionSupported;
import ru.ancndz.timeapp.user.domain.UserInfo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сущность информации о сотрудничестве.
 *
 * @author Anton Utkaev
 * @since 2024.04.19
 */
@Entity
@Table(name = "timetable_cooperate_info")
@IdClass(CooperateInfo.PrimaryKey.class)
@EntityListeners(AuditingEntityListener.class)
public class CooperateInfo implements DomainEntity<CooperateInfo.PrimaryKey>, SoftDeletionSupported {

    public static final String CN_WORKER_ID = "worker_id";

    public static final String CN_CLIENT_ID = "client_id";

    @Id
    @JoinColumn(name = CN_WORKER_ID, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private UserInfo worker;

    @Id
    @JoinColumn(name = CN_CLIENT_ID, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private UserInfo client;

    @Column
    private String info;

    @Column(updatable = false)
    @CreatedDate
    private LocalDate coopDate;

    @Column
    private boolean active = false;

    @Column
    private LocalDateTime archivedAt;

    @Transient
    private transient boolean isNew = false;

    @Override
    public PrimaryKey getId() {
        final PrimaryKey primaryKey = new PrimaryKey();
        primaryKey.setWorker(worker.getId());
        primaryKey.setClient(client.getId());
        return primaryKey;
    }

    public static class PrimaryKey implements Serializable {

        private String worker;

        private String client;

        public String getWorker() {
            return worker;
        }

        public void setWorker(String worker) {
            this.worker = worker;
        }

        public String getClient() {
            return client;
        }

        public void setClient(String client) {
            this.client = client;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            PrimaryKey that = (PrimaryKey) o;
            return Objects.equals(worker, that.worker) && Objects.equals(client, that.client);
        }

        @Override
        public int hashCode() {
            return 31 * client.hashCode() + worker.hashCode();
        }
    }

    @Override
    public boolean isDeleted() {
        return archivedAt != null;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.archivedAt = deleted ? LocalDateTime.now() : null;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public UserInfo getWorker() {
        return worker;
    }

    public void setWorker(UserInfo worker) {
        this.worker = worker;
    }

    public UserInfo getClient() {
        return client;
    }

    public void setClient(UserInfo client) {
        this.client = client;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public LocalDate getCoopDate() {
        return coopDate;
    }

    public void setCoopDate(LocalDate coopDate) {
        this.coopDate = coopDate;
    }

    public static Builder newCooperateInfo() {
        return new Builder();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public static class Builder {

        private UserInfo worker;

        private UserInfo client;

        private String info;

        private Builder() {
        }

        public Builder withWorker(UserInfo worker) {
            this.worker = worker;
            return this;
        }

        public Builder withClient(UserInfo client) {
            this.client = client;
            return this;
        }

        public Builder withInfo(String info) {
            this.info = info;
            return this;
        }

        public CooperateInfo build() {
            final CooperateInfo cooperateInfo = new CooperateInfo();
            cooperateInfo.setWorker(worker);
            cooperateInfo.setClient(client);
            cooperateInfo.setNew(true);
            if (info != null) {
                cooperateInfo.setInfo(info);
            }
            return cooperateInfo;
        }
    }
}
