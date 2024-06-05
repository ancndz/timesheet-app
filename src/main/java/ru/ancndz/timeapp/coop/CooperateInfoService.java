package ru.ancndz.timeapp.coop;

import ru.ancndz.timeapp.coop.domain.CooperateInfo;
import ru.ancndz.timeapp.user.domain.UserInfo;

import java.util.List;

/**
 * Сервис для работы с информацией о сотрудничестве.
 *
 * @author Anton Utkaev
 * @since 2024.05.12
 */
public interface CooperateInfoService {

    /**
     * Создание информации о сотрудничестве.
     * 
     * @param clientId
     *            айди клиента
     * @param workerId
     *            айди работника
     * @param description
     *            описание
     * @return информация о сотрудничестве
     */
    CooperateInfo createCooperateInfo(String clientId, String workerId, String description);

    /**
     * Получение информации о сотрудничестве для работника.
     * 
     * @param user
     *            работник
     * @return список информации о сотрудничестве
     */
    List<CooperateInfo> getWorkerCooperateInfos(UserInfo user);

    /**
     * Получение информации о сотрудничестве для клиента.
     * 
     * @param user
     *            клиент
     * @return список информации о сотрудничестве
     */
    List<CooperateInfo> getUserCooperateInfos(UserInfo user);

    /**
     * Удаление информации о сотрудничестве.
     * 
     * @param info
     *            информация о сотрудничестве
     */
    void deleteCooperateInfo(CooperateInfo info);

    /**
     * Сохранение информации о сотрудничестве.
     * 
     * @param item
     *            информация о сотрудничестве
     */
    void save(CooperateInfo item);

    /**
     * Принятие сотрудничество.
     * 
     * @param sender
     *            отправитель
     * @param addressee
     *            адресат
     */
    void acceptCooperation(UserInfo sender, UserInfo addressee);
}
