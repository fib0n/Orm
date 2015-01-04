package hh.homework.transactionexample.players;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by fib on 04/01/15.
 */
@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public final int id;

    @Column(name = "club_id")
    public final int clubId;
    public final String name;
    public final BigDecimal price;

    public Player(final int clubId, final String name, final BigDecimal price) {
        this.id = 0;
        this.clubId = clubId;
        this.name = name;
        this.price = price;
    }

    private Player(final int id, final int clubId, final String name, final BigDecimal price) {
        this.id = id;
        this.clubId = clubId;
        this.name = name;
        this.price = price;
    }

    @Deprecated
    Player() {
        this.id = -1;
        this.clubId = -1;
        this.name = "";
        this.price = new BigDecimal(0);
    }

    public Player withClub(final int clubId) {
        return new Player(this.id, clubId, this.name, this.price);
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        final Player player = (Player) that;
        return this.id == player.id && this.id > 0;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public String toString() {
        return String.format(
                "%s{id=%d, clubId=%d, name='%s', price='%s'}",
                getClass().getSimpleName(), this.id, this.clubId, this.name, price.toPlainString()
        );
    }
}
