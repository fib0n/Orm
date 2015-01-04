package hh.homework.transactionexample.players;

import hh.homework.transactionexample.HibernateUtils;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class PlayerDAOTest {
    private static SessionFactory sessionFactory;
    private static PlayerDAO dao;

    private static Configuration hibernateTestConfig() {
        return HibernateUtils.configuration
                //.setProperty("hibernate.connection.autocommit", "true")
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
    }

    @BeforeClass
    public static void init() throws Exception {
        sessionFactory = HibernateUtils.buildSessionFactory(hibernateTestConfig());
        HibernateUtils.initDb(sessionFactory.getCurrentSession());
        dao = new PlayerDAO(sessionFactory);
    }

    @AfterClass
    public static void close() throws Exception {
        if (sessionFactory != null)
            sessionFactory.close();
    }

    @Test
    public void testGet() throws Exception {
        int id = 1;
        inTransaction(() -> {
            Optional<Player> playerMaybe = dao.get(id);
            assertTrue(playerMaybe.isPresent());
            assertEquals(playerMaybe.get().id, id);
            return Optional.empty();
        });
    }

    @Test
    public void testAddOrUpdate() throws Exception {
        final Player player = inTransaction(() -> {
            final Player _player = new Player(2, "Ребров", new BigDecimal(1000000));
            dao.addOrUpdate(_player);
            return _player;
        });
        final int playerId = player.id;
        assertTrue(playerId > 0);

        final int newClubId = 1;
        final Player playerToUpdate = player.withClub(newClubId);
        final Player playerUpdated = inTransaction(() -> {
            dao.addOrUpdate(playerToUpdate);
            final Optional<Player> _playerMaybe = dao.get(playerId);
            assertTrue(_playerMaybe.isPresent());
            return _playerMaybe.get();
        });
        assertEquals(playerToUpdate.id, playerUpdated.id);
        assertEquals(playerUpdated.clubId, newClubId);
    }

    @Test
    public void testDelete() throws Exception {
        final int id = 1;
        inTransaction(() -> {
            final Optional<Player> playerMaybe = dao.get(id);
            assertTrue(playerMaybe.isPresent());
            dao.delete(id);
            return Optional.empty();
        });

        inTransaction(() -> {
            final Optional<Player> deletedPlayerMaybe = dao.get(id);
            assertFalse(deletedPlayerMaybe.isPresent());
            return Optional.empty();
        });
    }

    private <T> T inTransaction(final Supplier<T> supplier) {
        final Transaction transaction = sessionFactory.getCurrentSession().beginTransaction();
        try {
            T result = supplier.get();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}
