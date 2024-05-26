package ru.ancndz.timeapp.user.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import ru.ancndz.timeapp.user.domain.User;

/**
 * Репозиторий для сущности пользователя.
 *
 * @author Anton Utkaev
 * @since 2024.05.11
 */
@Repository
public interface UserRepository extends JpaRepository<User, String>, QuerydslPredicateExecutor<User> {

    /**
     * Поиск пользователя по имени пользователя или по электронной почте.
     *
     * @param username
     *            имя пользователя
     * @param email
     *            электронная почта
     * @return найденный пользователь
     */
    UserDetails findByUsernameOrUserInfoEmail(String username, String email);

}
