package ru.ancndz.timeapp.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.ancndz.timeapp.user.domain.User;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сервис ролей пользователей.
 *
 * @author Anton Utkaev
 * @since 2024.05.14
 */
public interface UserRoleService {

    /**
     * Префикс роли.
     */
    String ROLE_PREFIX = "ROLE_";

    /**
     * Карта ролей и их прав.
     */
    Map<String, GrantedAuthority> ROLE_GRANTED_AUTHORITY_MAP = Stream.of(Role.USER, Role.WORKER, Role.ADMIN)
            .collect(Collectors.toMap(Function.identity(), UserRoleService::createSimpleAuthority));

    /**
     * Создание простой роли.
     *
     * @param role
     *            роль
     * @return роль
     */
    static GrantedAuthority createSimpleAuthority(String role) {
        return new SimpleGrantedAuthority(ROLE_PREFIX + role);
    }

    /**
     * Проверка наличия роли у пользователя.
     *
     * @param user
     *            пользователь
     * @param role
     *            роль
     * @return {@code true} если роль присутствует, иначе {@code false}
     */
    boolean hasRole(User user, String role);

    /**
     * Добавление роли пользователю.
     *
     * @param user
     *            пользователь
     * @param roles
     *            роли
     */
    void addRole(User user, String... roles);

    /**
     * Удаление роли у пользователя.
     *
     * @param user
     *            пользователь
     * @param roles
     *            роли
     */
    void removeRole(User user, String... roles);

    /**
     * Заблокировать пользователя.
     *
     * @param pickedUser
     *            выбранный пользователь
     * @param b
     *            {@code true} если пользователь заблокирован, иначе {@code false}
     */
    void lockUser(User pickedUser, boolean b);
}
