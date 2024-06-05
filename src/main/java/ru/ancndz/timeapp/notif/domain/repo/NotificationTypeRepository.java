package ru.ancndz.timeapp.notif.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.ancndz.timeapp.notif.domain.NotificationType;

/**
 * Репозиторий типов уведомлений.
 *
 * @author Anton Utkaev
 * @since 2024.06.02
 */
@Repository
public interface NotificationTypeRepository
        extends JpaRepository<NotificationType, String>, QuerydslPredicateExecutor<NotificationType> {

    NotificationType findBySystemName(String systemName);
}
