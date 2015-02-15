package hh.homework.transactionexample.players;

import hh.homework.transactionexample.common.EntityDAO;

import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by fib on 04/01/15.
 */
public class PlayerService {
    private final EntityDAO<Player> playerDAO;

    @Inject
    public PlayerService(final EntityDAO<Player> playerDAO) {
        this.playerDAO = checkNotNull(playerDAO);
    }

    public Optional<Player> get(final int id) {
        return playerDAO.get(id);
    }

    public void changeClub(final Player player, final int clubId) {
        playerDAO.addOrUpdate(player.withClub(clubId));
    }
}
