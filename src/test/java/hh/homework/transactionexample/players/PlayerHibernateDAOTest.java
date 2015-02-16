package hh.homework.transactionexample.players;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import hh.homework.transactionexample.HibernateUtils;
import hh.homework.transactionexample.IocModule;
import hh.homework.transactionexample.IocTestModule;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class PlayerHibernateDAOTest {
    private static SessionFactory sessionFactory;
    private static PlayerHibernateDAO dao;

    @BeforeClass
    public static void init() throws Exception {
        final Injector injector = Guice.createInjector(Modules.override(new IocModule()).with(new IocTestModule()));
        sessionFactory = injector.getInstance(SessionFactory.class);
        HibernateUtils.initDb(sessionFactory.getCurrentSession());
        dao = injector.getInstance(PlayerHibernateDAO.class);
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
    public void testInsertAndUpdate() throws Exception {
        final Player player = inTransaction(() -> {
            final Player _player = new Player(2, "Ребров", new BigDecimal(1000000));
            return dao.insert(_player);
        });

        final int playerId = player.id;
        assertTrue(playerId > 0);

        final int newClubId = 1;
        final Player playerToUpdate = player.withClub(newClubId);
        final Player playerUpdated = inTransaction(() -> {
            dao.update(playerToUpdate);
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
