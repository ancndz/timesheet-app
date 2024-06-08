package ru.ancndz.timeapp.security.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ancndz.timeapp.security.UserRoleService;
import ru.ancndz.timeapp.user.domain.User;
import ru.ancndz.timeapp.user.domain.repo.UserRepository;

/**
 * Реализация сервиса ролей пользователей.
 *
 * @author Anton Utkaev
 * @since 2024.05.30
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRepository userRepository;

    public UserRoleServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean hasRole(User user, String role) {
        return user.getAuthorities().contains(UserRoleService.ROLE_GRANTED_AUTHORITY_MAP.get(role));
    }

    @Override
    @Transactional
    public void addRole(User user, String... roles) {
        final User currentUser = userRepository.getReferenceById(user.getId());
        for (String s : roles) {
            currentUser.addRole(UserRoleService.ROLE_GRANTED_AUTHORITY_MAP.get(s));
        }
    }

    @Override
    @Transactional
    public void removeRole(User user, String... roles) {
        final User currentUser = userRepository.getReferenceById(user.getId());
        for (String s : roles) {
            currentUser.revokeRole(UserRoleService.ROLE_GRANTED_AUTHORITY_MAP.get(s));
        }
    }

    @Override
    @Transactional
    public void lockUser(User pickedUser, boolean b) {
        pickedUser.setAccountNonLocked(b);
    }

}
