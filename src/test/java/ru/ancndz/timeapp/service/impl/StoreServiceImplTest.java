package ru.ancndz.timeapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ancndz.timeapp.AbstractTest;
import ru.ancndz.timeapp.core.StoreContext;
import ru.ancndz.timeapp.core.impl.StoreServiceImpl;
import ru.ancndz.timeapp.user.domain.User;
import ru.ancndz.timeapp.user.domain.UserInfo;
import ru.ancndz.timeapp.user.domain.repo.UserInfoRepository;
import ru.ancndz.timeapp.user.domain.repo.UserRepository;

import java.util.Arrays;

/**
 * Тесты для {@link StoreServiceImpl}.
 *
 * @author Anton Utkaev
 * @since 2024.05.11
 */
class StoreServiceImplTest extends AbstractTest {

    @Autowired
    private StoreServiceImpl service;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Проверка сохранения одной сущности.
     */
    @Test
    void storeOneEntity() {
        final UserInfo user = UserInfo.newUserInfo().withName("test").build();

        final UserInfo savedUser = doInTransactionWithRes(false, () -> {
            service.store(new StoreContext(user));
            return userInfoRepository.findById(user.getId()).orElse(null);
        });

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(user.getId());
        assertThat(savedUser.getName()).isEqualTo(user.getName());
    }

    /**
     * Проверка сохранения нескольких сущностей.
     */
    @Test
    void storeManyEntities() {
        final UserInfo user = UserInfo.newUserInfo().withName("test").build();
        final User userDetails =
                User.newUser().withUserInfo(user).withUsername("test").withPassword("test").build();

        final User savedUserDetails = doInTransactionWithRes(false, () -> {
            service.store(new StoreContext(Arrays.asList(userDetails, user)));
            return userRepository.findById(user.getId()).orElse(null);
        });

        assertThat(savedUserDetails).isNotNull();
        assertThat(savedUserDetails.getId()).isEqualTo(userDetails.getId());
        assertThat(savedUserDetails.getUserInfo().getId()).isEqualTo(user.getId());
    }

}