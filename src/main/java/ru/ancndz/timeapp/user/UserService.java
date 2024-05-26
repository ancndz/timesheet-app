package ru.ancndz.timeapp.user;

import jakarta.annotation.Nonnull;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.ancndz.timeapp.user.domain.User;
import ru.ancndz.timeapp.user.domain.UserInfo;

import java.util.List;

/**
 * Сервис пользователей.
 *
 * @author Anton Utkaev
 * @since 2024.04.19
 */
public interface UserService extends UserDetailsService {

    /**
     * Поиск пользователя по идентификатору.
     *
     * @param id
     *            идентификатор пользователя
     * @return найденный пользователь
     */
    @Nonnull
    User findUserById(String id);

    /**
     * Поиск пользователя по значению.
     *
     * @param value
     *            значение
     * @return найденный пользователь
     */
    List<UserInfo> searchUsersByValue(String value);

    /**
     * Проверка существования пользователя по имени.
     *
     * @param currentUser
     *            текущий пользователь
     * @param name
     *            имя
     * @return {@code true} если пользователь существует, иначе {@code false}
     */
    boolean existsUserByName(User currentUser, String name);

    /**
     * Проверка существования пользователя по электронной почте.
     *
     * @param currentUserInfo
     *            текущая информация о пользователе
     * @param email
     *            электронная почта
     * @return {@code true} если пользователь существует, иначе {@code false}
     */
    boolean existsUserByEmail(UserInfo currentUserInfo, String email);

    /**
     * Проверка существования пользователя по номеру телефона.
     *
     * @param currentUserInfo
     *            текущая информация о пользователе
     * @param phoneNumber
     *            номер телефона
     * @return {@code true} если пользователь существует, иначе {@code false}
     */
    boolean existsUserByPhoneNumber(UserInfo currentUserInfo, String phoneNumber);

    void saveUser(UserInfo user);

    /**
     * Сохранение пользователя.
     *
     * @param user
     *            пользователь
     */
    void saveUser(User user);
}
