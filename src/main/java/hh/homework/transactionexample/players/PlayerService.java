package hh.homework.transactionexample.players;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by fib on 04/01/15.
 */
public class PlayerService {
    private final PlayerDAO playerDAO;

    public PlayerService(final PlayerDAO playerDAO) {
        this.playerDAO = checkNotNull(playerDAO);
    }

    public Optional<Player> get(final int id) {
        return playerDAO.get(id);
    }

    public void changeClub(final Player player, final int clubId) {
        playerDAO.addOrUpdate(player.withClub(clubId));
    }
}
