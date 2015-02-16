package hh.homework.transactionexample.common;

import java.util.Optional;

/**
 * Created by fib on 04/01/15.
 */
public interface EntityDAO<T> {
    Optional<T> get(final int id);

    T insert(final T entity);

    void update(final T entity);

    void delete(final int id);
}
