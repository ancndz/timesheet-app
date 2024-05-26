package ru.ancndz.timeapp.user.impl;

import org.springframework.stereotype.Component;
import ru.ancndz.timeapp.security.DomainValidator;
import ru.ancndz.timeapp.security.ValidationContext;
import ru.ancndz.timeapp.security.ValidationException;
import ru.ancndz.timeapp.user.domain.QUserInfo;
import ru.ancndz.timeapp.user.domain.UserInfo;
import ru.ancndz.timeapp.user.domain.repo.UserInfoRepository;

/**
 * Валидатор для сущности информации о пользователе.
 *
 * @author Anton Utkaev
 * @since 2024.05.26
 */
@Component
public class UserInfoValidator implements DomainValidator {

    public static final String EMAIL_ERROR_CODE = "A user with this email already exists";

    public static final String PHONE_ERROR_CODE = "A user with this phone already exists";

    private final UserInfoRepository userInfoRepository;

    public UserInfoValidator(final UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    private boolean isEmailNotUnique(UserInfo user) {
        final QUserInfo qUserInfo = QUserInfo.userInfo;
        return userInfoRepository
                .exists(QUserInfo.userInfo.email.eq(user.getEmail()).and(qUserInfo.id.ne(user.getId())));
    }

    private boolean isPhoneNotUnique(UserInfo user) {
        final QUserInfo qUserInfo = QUserInfo.userInfo;
        return userInfoRepository
                .exists(QUserInfo.userInfo.phoneNumber.eq(user.getPhoneNumber()).and(qUserInfo.id.ne(user.getId())));
    }

    /**
     * Проверка на уникальность электронной почты и номера телефона.
     *
     * @param context
     *            контекст валидации
     * @throws ValidationException
     *             исключение валидации
     */
    @Override
    public void validate(ValidationContext context) throws ValidationException {
        for (Object entity : context.getEntities()) {
            if (entity instanceof UserInfo user) {
                if (user.getEmail() != null && isEmailNotUnique(user)) {
                    context.addError(EMAIL_ERROR_CODE);
                }
                if (user.getPhoneNumber() != null && isPhoneNotUnique(user)) {
                    context.addError(PHONE_ERROR_CODE);
                }
            }
        }
    }
}
