package ru.ancndz.timeapp.coop.domain.repo;

import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.ancndz.timeapp.coop.domain.CooperateInfo;
import ru.ancndz.timeapp.user.domain.UserInfo;

import java.util.List;

/**
 * Репозиторий для работы с сущностью информации о сотрудничестве.
 *
 * @author Anton Utkaev
 * @since 2024.04.19
 */
@Repository
public interface CooperateInfoRepository
        extends JpaRepository<CooperateInfo, CooperateInfo.PrimaryKey>, QuerydslPredicateExecutor<CooperateInfo> {

    @Nullable
    CooperateInfo findByClientIdAndWorkerId(String clientId, String workerId);

    List<CooperateInfo> findAllByWorkerAndArchivedAtIsNull(UserInfo worker);

    List<CooperateInfo> findAllByClientAndArchivedAtIsNull(UserInfo client);

}
