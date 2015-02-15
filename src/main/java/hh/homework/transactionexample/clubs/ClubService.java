package hh.homework.transactionexample.clubs;

import hh.homework.transactionexample.common.EntityDAO;
import hh.homework.transactionexample.players.Player;
import hh.homework.transactionexample.players.PlayerService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by fib on 04/01/15.
 */
public class ClubService {
    private final SessionFactory sessionFactory;
    private final EntityDAO<Club> clubDAO;
    private final PlayerService playerService;

    @Inject
    public ClubService(final SessionFactory sessionFactory, final EntityDAO<Club> clubDAO, final PlayerService playerService) {
        this.sessionFactory = checkNotNull(sessionFactory);
        this.clubDAO = checkNotNull(clubDAO);
        this.playerService = checkNotNull(playerService);
    }

    public void SellPlayer(final int playerId, final int clubCustomerId) {
        inTransaction(() -> {
            final Optional<Player> playerMaybe = playerService.get(playerId);

            if (!playerMaybe.isPresent())
                return false;

            final Player player = playerMaybe.get();
            final Club clubOwner = clubDAO.get(player.clubId)
                    .orElseThrow(() -> new RuntimeException("Игрок принадлежит несуществующему клубу"));

            if (clubOwner.getId() == clubCustomerId)
                throw new RuntimeException("Игрок уже и так в этом клубе");

            final Optional<Club> clubCustomerMaybe = clubDAO.get(clubCustomerId);
            if (!clubCustomerMaybe.isPresent())
                throw new RuntimeException("Не существует клуба, который хочет купить игрока");

            final Club clubCustomer = clubCustomerMaybe.get();
            final BigDecimal amount = player.price;
            if (clubCustomer.balance.compareTo(amount) < 0)
                throw new RuntimeException("Не хватает денег на счете для покупки игрока");

            clubDAO.addOrUpdate(clubOwner.changeBalance(amount));
            clubDAO.addOrUpdate(clubCustomer.changeBalance(amount.negate()));
            playerService.changeClub(player, clubCustomerId);

            return true;
        });
    }

    private void inTransaction(final BooleanSupplier supplier) {
        final Transaction transaction = session().beginTransaction();
        //use doReturningWork for jdbc and hibernate in one transaction
        session().doWork(connection -> {
            if (clubDAO instanceof ClubJDBCDAO)
                ((ClubJDBCDAO) clubDAO).setConnection(connection);   //dirty-dirty hack =(
            try {
                supplier.getAsBoolean();
                transaction.commit();
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            } finally {
                if (clubDAO instanceof ClubJDBCDAO)
                    ((ClubJDBCDAO) clubDAO).setConnection(null);
            }
        });
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }
}
