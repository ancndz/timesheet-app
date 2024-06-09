package ru.ancndz.timeapp.coop.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ancndz.timeapp.coop.CooperateInfoService;
import ru.ancndz.timeapp.coop.domain.CooperateInfo;
import ru.ancndz.timeapp.coop.domain.repo.CooperateInfoRepository;
import ru.ancndz.timeapp.core.StoreContext;
import ru.ancndz.timeapp.core.StoreService;
import ru.ancndz.timeapp.notif.NotificationService;
import ru.ancndz.timeapp.notif.domain.NotificationTypeSystemName;
import ru.ancndz.timeapp.user.domain.UserInfo;
import ru.ancndz.timeapp.user.domain.repo.UserInfoRepository;

import java.util.List;

/**
 * Реализация сервиса для работы с информацией о сотрудничестве.
 *
 * @author Anton Utkaev
 * @since 2024.05.12
 */
@Service
public class CooperateInfoServiceImpl implements CooperateInfoService {

    private final CooperateInfoRepository cooperateInfoRepository;

    private final UserInfoRepository userInfoRepository;

    private final NotificationService notificationService;

    private final StoreService storeService;

    public CooperateInfoServiceImpl(final CooperateInfoRepository cooperateInfoRepository,
            final UserInfoRepository userInfoRepository,
            final NotificationService notificationService,
            final StoreService storeService) {
        this.cooperateInfoRepository = cooperateInfoRepository;
        this.userInfoRepository = userInfoRepository;
        this.notificationService = notificationService;
        this.storeService = storeService;
    }

    @Override
    @Transactional
    public CooperateInfo createCooperateInfo(String clientId, String workerId, String info) {
        CooperateInfo existing = cooperateInfoRepository.findByClientIdAndWorkerId(clientId, workerId);
        if (existing != null) {
            existing.setDeleted(false);
            existing.setNew(true);
            existing.setInfo(info);
            existing.setActive(false);
        } else {
            existing = CooperateInfo.newCooperateInfo()
                    .withClient(userInfoRepository.getReferenceById(clientId))
                    .withWorker(userInfoRepository.getReferenceById(workerId))
                    .withInfo(info)
                    .build();
        }
        storeService.store(new StoreContext(existing));
        return cooperateInfoRepository.findByClientIdAndWorkerId(clientId, workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CooperateInfo> getWorkerCooperateInfos(final UserInfo user) {
        return cooperateInfoRepository.findAllByWorkerAndArchivedAtIsNull(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CooperateInfo> getUserCooperateInfos(final UserInfo user) {
        return cooperateInfoRepository.findAllByClientAndArchivedAtIsNull(user);
    }

    @Override
    @Transactional
    public void deleteCooperateInfo(final CooperateInfo info) {
        info.setDeleted(true);
        storeService.store(new StoreContext(info));
    }

    @Override
    @Transactional
    public void save(CooperateInfo item) {
        storeService.store(new StoreContext(item));
    }

    @Override
    public void acceptCooperation(UserInfo sender, UserInfo addressee) {
        final CooperateInfo cooperateInfo =
                cooperateInfoRepository.findByClientIdAndWorkerId(addressee.getId(), sender.getId());
        if (cooperateInfo != null) {
            StoreContext storeContext = new StoreContext(cooperateInfo);
            cooperateInfo.setActive(true);

            notificationService
                    .createNotification(sender, addressee, NotificationTypeSystemName.COOP_ACCEPTED, storeContext);

            storeService.store(storeContext);
        }
    }

}
