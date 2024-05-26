package ru.ancndz.timeapp.user.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.ancndz.timeapp.user.domain.UserInfo;

/**
 * Репозиторий для сущности информации о пользователе.
 *
 * @author Anton Utkaev
 * @since 2024.04.18
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, String>, QuerydslPredicateExecutor<UserInfo> {
}
