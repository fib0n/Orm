package hh.homework.transactionexample.clubs;

import hh.homework.transactionexample.common.EntityDAO;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by fib on 04/01/15.
 */
public class ClubDAO implements EntityDAO<Club> {
    private final DataSource dataSource;
    private Connection connectionOnlyForTransaction;

    public ClubDAO(final DataSource dataSource) {
        this.dataSource = checkNotNull(dataSource);
    }

    void setConnection(final Connection connection) {
        this.connectionOnlyForTransaction = connection;
    }

    @Override
    public Optional<Club> get(final int id) {
        final String exceptionMessage = "failed to get " + id;
        return dbConnect(connection -> {
            final String query = "SELECT id, name, balance FROM clubs WHERE id = ?";
            try (final PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, id);

                try (final ResultSet resultSet = statement.executeQuery()) {

                    return Optional.ofNullable(
                            resultSet.next() ?
                                    new Club(id, resultSet.getString("name"), resultSet.getBigDecimal("balance")) :
                                    null);
                }
            } catch (SQLException e) {
                throw new RuntimeException(exceptionMessage, e);
            }
        }, exceptionMessage);
    }

    @Override
    public Club addOrUpdate(final Club club) {
        if (club.isNew()) //hacked dispatching
            return insert(club);
        else {
            update(club);
            return club;
        }
    }

    @Override
    public void delete(final int id) {
        final String exceptionMessage = "failed to remove club by id" + id;
        dbConnect(connection -> {
            final String query = "DELETE FROM clubs WHERE id = ?";
            try (final PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, id);

                statement.executeUpdate();
                return Optional.empty();
            } catch (SQLException e) {
                throw new RuntimeException(exceptionMessage, e);
            }
        }, exceptionMessage);
    }

    private Club insert(final Club newClub) {
        final String exceptionMessage = "failed to insert " + newClub;
        return dbConnect(connection -> {
            final String query = "INSERT INTO clubs (name, balance) VALUES (?, ?)";
            try (final PreparedStatement statement =
                         connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, newClub.name);
                statement.setBigDecimal(2, newClub.balance);

                statement.executeUpdate();

                try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    return new Club(generatedKeys.getInt(1), newClub.name, newClub.balance);
                }
            } catch (SQLException e) {
                throw new RuntimeException(exceptionMessage, e);
            }
        }, exceptionMessage);
    }

    private void update(final Club club) {
        final String exceptionMessage = "failed to update " + club;
        dbConnect(connection -> {
            final String query = "UPDATE clubs SET name = ?, balance = ? WHERE id = ?";
            try (final PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, club.name);
                statement.setBigDecimal(2, club.balance);
                statement.setInt(3, club.getId());

                statement.executeUpdate();
                return club;
            } catch (SQLException e) {
                throw new RuntimeException(exceptionMessage, e);
            }
        }, exceptionMessage);
    }

    private <R> R dbConnect(final Function<Connection, R> func, final String exceptionMessage) {
        if (connectionOnlyForTransaction != null)
            return func.apply(connectionOnlyForTransaction);

        try (Connection connection = dataSource.getConnection()) {
            return func.apply(connection);
        } catch (SQLException e) {
            throw new RuntimeException(exceptionMessage, e);
        }
    }
}
