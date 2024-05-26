package ru.ancndz.timeapp.security;

import org.springframework.security.core.GrantedAuthority;
import ru.ancndz.timeapp.user.domain.User;

import java.util.Set;

/**
 * Сервис авторизации.
 *
 * @author Anton Utkaev
 * @since 2024.05.14
 */
public interface AuthorizationService {

    /**
     * Префикс роли.
     */
    String ROLE_PREFIX = "ROLE_";

    /**
     * Проверка на администратора.
     *
     * @param user
     *            пользователь
     * @return является ли пользователь администратором
     */
    boolean isAdministrator(User user);

    /**
     * Проверка на работника.
     *
     * @param user
     *            пользователь
     * @return является ли пользователь работником
     */
    boolean isWorker(User user);

    /**
     * Проверка на пользователя.
     *
     * @param user
     *            пользователь
     * @return является ли пользователь пользователем
     */
    boolean isUser(User user);

    /**
     * Получение роли.
     *
     * @param role
     *            роль
     * @return роль
     */
    GrantedAuthority getAuthority(String role);

    /**
     * Получение ролей.
     *
     * @param roles
     *            роли
     * @return роли
     */
    Set<GrantedAuthority> getAuthorities(String... roles);
}
