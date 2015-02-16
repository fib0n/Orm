package hh.homework.transactionexample.players;

import hh.homework.transactionexample.common.EntityDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import java.util.Optional;

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
        final Player player = (Player) session().get(Player.class, id);
        return Optional.ofNullable(player);
    }

    @Override
    public Player insert(final Player player) {
        session().save(player);
        return player;
    }

    @Override
    public void update(final Player player) {
        session().merge(player);
    }

    @Override
    public void delete(final int id) {
        session().createQuery("DELETE Player WHERE id = :id")
                .setInteger("id", id)
                .executeUpdate();
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }
}
