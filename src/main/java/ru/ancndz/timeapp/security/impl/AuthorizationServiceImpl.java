package ru.ancndz.timeapp.security.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import ru.ancndz.timeapp.user.domain.Role;
import ru.ancndz.timeapp.user.domain.User;
import ru.ancndz.timeapp.security.AuthorizationService;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса авторизации.
 *
 * @author Anton Utkaev
 * @since 2024.05.14
 */
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Override
    public boolean isAdministrator(User user) {
        return userContainsRole(user, Role.ADMIN);
    }

    @Override
    public boolean isWorker(User user) {
        return userContainsRole(user, Role.WORKER);
    }

    @Override
    public boolean isUser(User user) {
        return userContainsRole(user, Role.USER);
    }

    private boolean userContainsRole(User user, String role) {
        return user.getAuthorities().contains(getAuthority(role));
    }

    @Override
    public GrantedAuthority getAuthority(String role) {
        return new SimpleGrantedAuthority(ROLE_PREFIX + role);
    }

    @Override
    public Set<GrantedAuthority> getAuthorities(String... roles) {
        return Arrays.stream(roles).map(this::getAuthority).collect(Collectors.toSet());
    }
}
