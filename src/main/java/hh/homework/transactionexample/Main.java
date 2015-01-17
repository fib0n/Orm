package hh.homework.transactionexample;

import hh.homework.transactionexample.clubs.ClubDAO;
import hh.homework.transactionexample.clubs.ClubService;
import hh.homework.transactionexample.players.PlayerDAO;
import hh.homework.transactionexample.players.PlayerService;
import org.hibernate.SessionFactory;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by fib on 04/01/15.
 */
class Main {

    public static void main(final String[] args) throws Exception {
        final SessionFactory sessionFactory = HibernateUtils
                .buildSessionFactory(HibernateUtils.getProdConfig());

        try {
            if (args.length > 0 && "reinit".equals(args[0]))
                HibernateUtils.initDb(sessionFactory.getCurrentSession());

            final ClubService clubService = new ClubService(
                    sessionFactory,
                    new ClubDAO(buildDataSource()),
                    new PlayerService(new PlayerDAO(sessionFactory)));
            final boolean result = clubService.SellPlayer(4, 1);
            System.out.println(result);
        } finally {
            sessionFactory.close();
        }
    }

    private static DataSource buildDataSource() throws SQLException {
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();

        dataSource.setUrl(DatabaseConfig.url);
        dataSource.setUser(DatabaseConfig.user);
        dataSource.setPassword(DatabaseConfig.password);
        return dataSource;
    }
}
