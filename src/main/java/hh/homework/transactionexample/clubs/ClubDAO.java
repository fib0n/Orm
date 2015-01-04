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
    private Connection connection;

    public ClubDAO(final DataSource dataSource) {
        this.dataSource = checkNotNull(dataSource);
    }

    void setConnection(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Club> get(final int id) {
        final String exceptionMessage = "failed to get " + id;
        return dbConnect(cn -> {
            final String query = "SELECT id, name, balance FROM clubs WHERE id = ?";
            try (final PreparedStatement statement = cn.prepareStatement(query)) {

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
        return club.isNew() ? insert(club) : update(club); //hacked dispatching
    }

    @Override
    public void delete(final int id) {
        final String exceptionMessage = "failed to remove club by id" + id;
        dbConnect(cn -> {
            final String query = "DELETE FROM clubs WHERE id = ?";
            try (final PreparedStatement statement = cn.prepareStatement(query)) {

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
        return dbConnect(cn -> {
            final String query = "INSERT INTO clubs (name, balance) VALUES (?, ?)";
            try (final PreparedStatement statement =
                         cn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

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

    private Club update(final Club club) {
        final String exceptionMessage = "failed to update " + club;
        return dbConnect(cn -> {
            final String query = "UPDATE clubs SET name = ?, balance = ? WHERE id = ?";
            try (final PreparedStatement statement = cn.prepareStatement(query)) {

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
        if (this.connection != null)
            return func.apply(connection);

        try (Connection connection = dataSource.getConnection()) {
            return func.apply(connection);
        } catch (SQLException e) {
            throw new RuntimeException(exceptionMessage, e);
        }
    }
}
