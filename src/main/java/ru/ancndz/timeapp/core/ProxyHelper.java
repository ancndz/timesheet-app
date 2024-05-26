package ru.ancndz.timeapp.core;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

/**
 * Вспомогательный класс для работы с прокси-объектами.
 *
 * @author Anton Utkaev
 * @since 2024.05.11
 */
public class ProxyHelper {

    public static <T> T initializeAndUnproxy(T obj) {
        T result = null;
        if (obj != null) {
            Hibernate.initialize(obj);
            if (obj instanceof HibernateProxy) {
                obj = (T) ((HibernateProxy) obj).getHibernateLazyInitializer().getImplementation();
            }
            result = obj;
        }
        return result;
    }

}
