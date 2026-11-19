package com.mini.spring.transaction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionSynchronizationManagerTest {

    private final DataSource dataSource = fake(DataSource.class);
    private final Connection connection = fake(Connection.class);

    @AfterEach
    void clearBinding() {
        TransactionSynchronizationManager.unbind(dataSource);
    }

    @Test
    void connectionIsBoundToTheCurrentThread() {
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        assertSame(connection, TransactionSynchronizationManager.getResource(dataSource));
    }

    @Test
    void unbindReturnsTheConnectionAndClearsTheThread() {
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        assertSame(connection, TransactionSynchronizationManager.unbind(dataSource));
        assertNull(TransactionSynchronizationManager.getResource(dataSource));
    }

    @Test
    void bindingTheSameDataSourceTwiceThrows() {
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        assertThrows(IllegalStateException.class,
                () -> TransactionSynchronizationManager.bindResource(dataSource, connection));
    }

    @Test
    void anotherThreadSeesNothing() throws InterruptedException {
        TransactionSynchronizationManager.bindResource(dataSource, connection);

        AtomicReference<Connection> seen = new AtomicReference<>();
        Thread other = new Thread(() -> seen.set(TransactionSynchronizationManager.getResource(dataSource)));
        other.start();
        other.join();

        assertNull(seen.get());
        assertSame(connection, TransactionSynchronizationManager.getResource(dataSource));
    }

    private static <T> T fake(Class<T> type) {
        return type.cast(Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[]{type},
                (proxy, called, args) -> switch (called.getName()) {
                    case "toString" -> "Fake" + type.getSimpleName();
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> null;
                }));
    }
}
