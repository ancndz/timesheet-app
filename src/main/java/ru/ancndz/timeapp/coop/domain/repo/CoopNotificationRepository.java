package ru.ancndz.timeapp.coop.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.ancndz.timeapp.coop.domain.CoopNotification;

/**
 * Репозиторий для работы с сущностью уведомлений о сотрудничестве.
 *
 * @author Anton Utkaev
 * @since 2024.05.18
 */
@Repository
public interface CoopNotificationRepository
        extends JpaRepository<CoopNotification, String>, QuerydslPredicateExecutor<CoopNotification> {

}
