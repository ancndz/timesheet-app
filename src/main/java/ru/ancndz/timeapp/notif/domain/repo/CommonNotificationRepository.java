package ru.ancndz.timeapp.notif.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.ancndz.timeapp.notif.domain.CommonNotification;

import java.util.List;

/**
 * Репозиторий уведомлений.
 *
 * @author Anton Utkaev
 * @since 2024.05.18
 */
@Repository
public interface CommonNotificationRepository
        extends JpaRepository<CommonNotification, String>, QuerydslPredicateExecutor<CommonNotification> {

    /**
     * Поиск всех уведомлений по айди пользователя.
     *
     * @param userId
     *            айди пользователя
     * @return список уведомлений
     */
    List<CommonNotification> findAllByUserIdAndArchivedAtIsNullOrderByCreatedAtDesc(String userId);

    /**
     * Поиск всех уведомлений по айди отправителя.
     *
     * @param senderId
     *            айди отправителя
     * @return список уведомлений
     */
    List<CommonNotification> findAllBySenderIdAndArchivedAtIsNullOrderByCreatedAtDesc(String senderId);

    /**
     * Проверка наличия уведомлений по айди пользователя.
     *
     * @param id
     *            айди пользователя
     * @return наличие уведомлений
     */
    boolean existsByUserIdAndArchivedAtIsNull(String id);

}
