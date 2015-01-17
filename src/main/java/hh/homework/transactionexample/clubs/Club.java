package hh.homework.transactionexample.clubs;

import java.math.BigDecimal;

/**
 * Created by fib on 04/01/15.
 */
public class Club {

    public final String name;
    public final BigDecimal balance;
    private final int id;

    public Club(final String name, final BigDecimal balance) {
        this.id = 0;
        this.name = name;
        this.balance = balance;
    }

    public Club(final int id, final String name, final BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public boolean isNew() {
        return id <= 0;
    }

    public Club withName(final String name) {
        return new Club(id, name, balance);
    }

    public Club withBalance(final BigDecimal balance) {
        return new Club(id, name, balance);
    }

    public Club changeBalance(final BigDecimal delta) {
        return withBalance(balance.add(delta));
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        final Club club = (Club) that;
        return id == club.id && !isNew();
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return String.format(
                "%s{id=%d, name='%s', balance='%s'}",
                getClass().getSimpleName(), id, name, balance.toPlainString()
        );
    }
}
