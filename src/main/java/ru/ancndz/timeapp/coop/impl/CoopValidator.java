package ru.ancndz.timeapp.coop.impl;

import org.springframework.stereotype.Component;
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

    public CoopValidator(final CooperateInfoRepository cooperateInfoRepository) {
        this.cooperateInfoRepository = cooperateInfoRepository;
    }

    private boolean isCoopAlreadyExists(final CooperateInfo cooperateInfo) {
        final QCooperateInfo qCooperateInfo = QCooperateInfo.cooperateInfo;
        return cooperateInfoRepository.exists(qCooperateInfo.worker.id.eq(cooperateInfo.getWorker().getId())
                .and(qCooperateInfo.client.id.eq(cooperateInfo.getClient().getId()))
                .and(qCooperateInfo.archivedAt.isNull()));
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
