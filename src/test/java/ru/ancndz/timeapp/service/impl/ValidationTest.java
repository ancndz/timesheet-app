package ru.ancndz.timeapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ancndz.timeapp.AbstractTest;
import ru.ancndz.timeapp.core.StoreContext;
import ru.ancndz.timeapp.core.StoreService;
import ru.ancndz.timeapp.security.ValidationException;
import ru.ancndz.timeapp.user.domain.QUser;
import ru.ancndz.timeapp.user.domain.User;
import ru.ancndz.timeapp.user.domain.repo.UserRepository;

/**
 * Тесты для валидации.
 *
 * @author Anton Utkaev
 * @since 2024.05.26
 */
public class ValidationTest extends AbstractTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreService storeService;

    /**
     * Проверка валидации.
     */
    @Test
    public void test() {
        createUser("test1");
        final QUser qUser = QUser.user;
        assertThat(userRepository.findAll(qUser.username.eq("test1"))).isNotNull();
        assertThatThrownBy(() -> createUser("test1")).isInstanceOf(ValidationException.class)
                .hasMessage("A user with this name already exists");
    }

    private void createUser(final String test1) {
        final User user = User.newUser().withUsername(test1).withPassword("test").build();
        doInTransaction(false, () -> storeService.store(new StoreContext(user)));
    }

}
