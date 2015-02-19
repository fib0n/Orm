package hh.homework.transactionexample.players;

import hh.homework.transactionexample.common.EntityDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.inject.Inject;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by fib on 04/01/15.
 */
public class PlayerHibernateDAO implements EntityDAO<Player> {
    private final SessionFactory sessionFactory;

    @Inject
    public PlayerHibernateDAO(final SessionFactory sessionFactory) {
        this.sessionFactory = checkNotNull(sessionFactory);
    }

    @Override
    public Optional<Player> get(final int id) {
        return inTransaction(() -> {
            final Player player = (Player) session().get(Player.class, id);
            return Optional.ofNullable(player);
        });
    }

    @Override
    public Player insert(final Player player) {
        return inTransaction(() -> {
            session().save(player);
            return player;
        });
    }

    @Override
    public void update(final Player player) {
        inTransaction(() -> {
            session().merge(player);
            return Optional.empty();
        });
    }

    @Override
    public void delete(final int id) {
        inTransaction(() -> {
            session().createQuery("DELETE Player WHERE id = :id")
                    .setInteger("id", id)
                    .executeUpdate();
            return Optional.empty();
        });
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    private <T> T inTransaction(final Supplier<T> supplier) {
        if (session().getTransaction().isActive())
            return supplier.get();

        final Transaction transaction = session().beginTransaction();
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
