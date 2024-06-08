package ru.ancndz.timeapp.user.impl;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ancndz.timeapp.core.StoreContext;
import ru.ancndz.timeapp.core.StoreService;
import ru.ancndz.timeapp.user.UserService;
import ru.ancndz.timeapp.user.domain.QUser;
import ru.ancndz.timeapp.user.domain.QUserInfo;
import ru.ancndz.timeapp.user.domain.User;
import ru.ancndz.timeapp.user.domain.UserInfo;
import ru.ancndz.timeapp.user.domain.repo.UserInfoRepository;
import ru.ancndz.timeapp.user.domain.repo.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Реализация сервиса пользователей.
 *
 * @author Anton Utkaev
 * @since 2024.04.19
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserInfoRepository userInfoRepository;

    private final UserRepository userRepository;

    private final StoreService storeService;

    public UserServiceImpl(final UserInfoRepository userInfoRepository,
            final UserRepository userRepository,
            final StoreService storeService) {
        this.userInfoRepository = userInfoRepository;
        this.userRepository = userRepository;
        this.storeService = storeService;
    }

    @Override
    @Transactional(readOnly = true)
    @Nonnull
    public User findUserById(final String id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Stream<User> findAll(int limit, int offset) {
        return userRepository.findAll(Pageable.ofSize(limit).withPage(offset / limit))
                .stream();
    }

    @Override
    public Stream<User> findAll(PageRequest of) {
        return userRepository.findAll(of).stream();
    }

    @Override
    public long countAll() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    @Nonnull
    public UserDetails loadUserByUsername(final String value) throws UsernameNotFoundException {
        return Optional.of(userRepository.findByUsernameOrUserInfoEmail(value, value))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserInfo> searchUsersByValue(final String value) {
        final QUserInfo qUserInfo = QUserInfo.userInfo;
        return StreamSupport
                .stream(userInfoRepository.findAll(qUserInfo.name.likeIgnoreCase('%' + value + '%')
                        .or(qUserInfo.email.likeIgnoreCase('%' + value + '%'))
                        .or(qUserInfo.phoneNumber.likeIgnoreCase('%' + value + '%'))).spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsUserByName(User currentUser, String name) {
        return userRepository.exists(QUser.user.username.eq(name).and(QUser.user.id.ne(currentUser.getId())));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsUserByEmail(UserInfo currentUserInfo, String email) {
        return userInfoRepository
                .exists(QUserInfo.userInfo.email.eq(email).and(QUserInfo.userInfo.id.ne(currentUserInfo.getId())));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsUserByPhoneNumber(UserInfo currentUserInfo, String phoneNumber) {
        return userInfoRepository.exists(
                QUserInfo.userInfo.phoneNumber.eq(phoneNumber).and(QUserInfo.userInfo.id.ne(currentUserInfo.getId())));
    }

    @Override
    @Transactional
    public void saveUser(UserInfo user) {
        storeService.store(new StoreContext(user));
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        storeService.store(new StoreContext(user));
    }
}
