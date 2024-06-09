package ru.ancndz.timeapp.coop.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ancndz.timeapp.coop.domain.CooperateInfo;
import ru.ancndz.timeapp.coop.domain.QCooperateInfo;
import ru.ancndz.timeapp.coop.domain.repo.CooperateInfoRepository;
import ru.ancndz.timeapp.security.DomainValidator;
import ru.ancndz.timeapp.security.ValidationContext;
import ru.ancndz.timeapp.security.ValidationException;

/**
 * Валидатор для проверки сотрудничества.
 *
 * @author Anton Utkaev
 * @since 2024.05.26
 */
@Component
public class CoopValidator implements DomainValidator {

    public static final String COOP_ERROR_CODE = "This coop already exists";

    private final CooperateInfoRepository cooperateInfoRepository;

    private final PlatformTransactionManager transactionManager;

    public CoopValidator(final CooperateInfoRepository cooperateInfoRepository,
            final PlatformTransactionManager transactionManager) {
        this.cooperateInfoRepository = cooperateInfoRepository;
        this.transactionManager = transactionManager;
    }

    private boolean isCoopAlreadyExists(final CooperateInfo cooperateInfo) {
        final QCooperateInfo qCooperateInfo = QCooperateInfo.cooperateInfo;
        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setReadOnly(true);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);

        return Boolean.TRUE.equals(transactionTemplate.execute(
                status -> cooperateInfoRepository.exists(qCooperateInfo.worker.id.eq(cooperateInfo.getWorker().getId())
                        .and(qCooperateInfo.client.id.eq(cooperateInfo.getClient().getId()))
                        .and(qCooperateInfo.archivedAt.isNull()))));
    }

    @Override
    public void validate(final ValidationContext context) throws ValidationException {
        for (Object entity : context.getEntities()) {
            if (entity instanceof CooperateInfo cooperateInfo) {
                if (cooperateInfo.isNew() && isCoopAlreadyExists(cooperateInfo)) {
                    context.addError(COOP_ERROR_CODE);
                }
            }
        }
    }
}
