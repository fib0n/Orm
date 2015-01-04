package hh.homework.transactionexample.clubs;

import hh.homework.transactionexample.players.Player;
import hh.homework.transactionexample.players.PlayerService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by fib on 04/01/15.
 */
public class ClubService {
    private final SessionFactory sessionFactory;
    private final ClubDAO clubDAO;
    private final PlayerService playerService;

    public ClubService(final SessionFactory sessionFactory, final ClubDAO clubDAO, final PlayerService playerService) {
        this.sessionFactory = checkNotNull(sessionFactory);
        this.clubDAO = checkNotNull(clubDAO);
        this.playerService = checkNotNull(playerService);
    }

    public Boolean SellPlayer(final int playerId, final int clubCustomerId) {
        return inTransaction(() -> {
            final Optional<Player> playerMaybe = this.playerService.get(playerId);

            if (!playerMaybe.isPresent())
                return false;

            final Player player = playerMaybe.get();
            final Club clubOwner = this.clubDAO.get(player.clubId)
                    .orElseThrow(() -> new RuntimeException("The player belongs to a non-existent club"));

            if (clubOwner.getId() == clubCustomerId)
                return false;

            final Optional<Club> clubCustomerMaybe = this.clubDAO.get(clubCustomerId);
            if (!clubCustomerMaybe.isPresent())
                return false;

            final Club clubCustomer = clubCustomerMaybe.get();
            final BigDecimal amount = player.price;
            if (clubCustomer.balance.compareTo(amount) < 0)
                return false;

            //use doWork for jdbc and hibernate in one transaction
            session().doWork(connection -> {
                this.clubDAO.setConnection(connection);  //dirty-dirty hack =(
                this.clubDAO.addOrUpdate(clubOwner.changeBalance(amount));
                this.clubDAO.addOrUpdate(clubCustomer.changeBalance(amount.negate()));
                this.clubDAO.setConnection(null);
            });
            this.playerService.changeClub(player, clubCustomerId);

            return true;
        });
    }

    private boolean inTransaction(final BooleanSupplier supplier) {
        final Transaction transaction = session().beginTransaction();
        try {
            final boolean result = supplier.getAsBoolean();
            transaction.commit();
            return result;
        } catch (RuntimeException e) {
            transaction.rollback();
            throw e;
        }
    }

    private Session session() {
        return this.sessionFactory.getCurrentSession();
    }
}