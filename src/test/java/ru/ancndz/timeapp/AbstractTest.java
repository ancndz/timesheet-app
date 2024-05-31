package ru.ancndz.timeapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

/**
 * Абстрактный класс для тестов.
 *
 * @author Anton Utkaev
 * @since 2024.04.18
 */
@SpringBootTest
@ActiveProfiles({ "in-memory" })
public class AbstractTest {

    @Autowired
    protected PlatformTransactionManager transactionManager;

    /**
     * Выполнить действие в транзакции.
     *
     * @param readonly
     *            флаг только для чтения
     * @param handler
     *            действие
     */
    protected void doInTransaction(final boolean readonly, final Runnable handler) {
        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        if (readonly) {
            transactionTemplate.setReadOnly(true);
        }
        transactionTemplate.execute(status -> {
            handler.run();
            return null;
        });
    }

    /**
     * Выполнить действие в транзакции с возвратом результата.
     *
     * @param <T>
     *            тип результата
     * @param readonly
     *            флаг только для чтения
     * @param handler
     *            действие
     * @return результат
     */
    protected <T> T doInTransactionWithRes(final boolean readonly, final Supplier<T> handler) {
        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        if (readonly) {
            transactionTemplate.setReadOnly(true);
        }
        return transactionTemplate.execute(status -> handler.get());
    }
}
