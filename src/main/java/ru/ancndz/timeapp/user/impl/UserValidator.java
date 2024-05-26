package ru.ancndz.timeapp.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ancndz.timeapp.security.DomainValidator;
import ru.ancndz.timeapp.security.ValidationContext;
import ru.ancndz.timeapp.user.domain.QUser;
import ru.ancndz.timeapp.user.domain.User;
import ru.ancndz.timeapp.user.domain.repo.UserRepository;

/**
 * Валидатор для сущности пользователя.
 *
 * @author Anton Utkaev
 * @since 2024.04.19
 */
@Component
public class UserValidator implements DomainValidator {

    public static final String ERROR_CODE = "A user with this name already exists";

    private final UserRepository userRepository;

    @Autowired
    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private boolean isNameNotUnique(User user) {
        final QUser qUser = QUser.user;
        return userRepository.exists(qUser.username.eq(user.getUsername()).and(qUser.id.ne(user.getId())));
    }

    @Override
    public void validate(ValidationContext context) {
        for (Object entity : context.getEntities()) {
            if (entity instanceof User user) {
                if (isNameNotUnique(user)) {
                    context.addError(ERROR_CODE);
                }
            }
        }
    }

}
