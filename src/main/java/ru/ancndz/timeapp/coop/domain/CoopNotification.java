package ru.ancndz.timeapp.coop.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import ru.ancndz.timeapp.notif.domain.CommonNotification;
import ru.ancndz.timeapp.notif.domain.NotificationType;

/**
 * Сущность уведомления о сотрудничестве.
 *
 * @author Anton Utkaev
 * @since 2024.05.18
 */
@Entity
@DiscriminatorValue("coop")
public class CoopNotification extends CommonNotification {

    @ManyToOne
    private CooperateInfo cooperateInfo;

    public CoopNotification() {
    }

    public CooperateInfo getCooperateInfo() {
        return cooperateInfo;
    }

    public void setCooperateInfo(CooperateInfo cooperateInfo) {
        this.cooperateInfo = cooperateInfo;
    }

    public static CoopBuilder newCoopNotification() {
        return new CoopNotification().new CoopBuilder();
    }

    public class CoopBuilder extends AbstractBuilder<CoopBuilder, CoopNotification> {

        CooperateInfo cooperateInfo;

        protected CoopBuilder() {
        }

        @Override
        protected CoopNotification createEmpty() {
            return new CoopNotification();
        }

        public CoopBuilder withCooperateInfo(CooperateInfo cooperateInfo) {
            this.cooperateInfo = cooperateInfo;
            return this;
        }

        @Override
        public CoopNotification build() {
            final CoopNotification notification = super.build();
            notification.setType(NotificationType.COOPERATION);
            notification.setCooperateInfo(cooperateInfo);
            return notification;
        }
    }
}
