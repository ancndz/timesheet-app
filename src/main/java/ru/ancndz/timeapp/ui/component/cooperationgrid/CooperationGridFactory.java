package ru.ancndz.timeapp.ui.component.cooperationgrid;

import ru.ancndz.timeapp.coop.CooperateInfoService;
import ru.ancndz.timeapp.user.domain.UserInfo;

/**
 * Фабрика для создания таблицы сотрудничества.
 *
 * @author Anton Utkaev
 * @since 2024.04.29
 */
public class CooperationGridFactory {

    /**
     * Создание таблицы сотрудничества.
     *
     * @param cooperateInfoService
     *            сервис сотрудничества
     * @param currentUserInfo
     *            информация о текущем пользователе
     * @param isMobile
     *            мобильное ли устройство
     * @return таблица сотрудничества
     */
    public static AbstractCooperationGrid createGrid(final CooperateInfoService cooperateInfoService,
            final UserInfo currentUserInfo,
            final boolean isMobile) {
        if (isMobile) {
            return new MobileCooperationGrid(cooperateInfoService, currentUserInfo);
        } else {
            return new DefaultCooperationGrid(cooperateInfoService, currentUserInfo);
        }
    }

}