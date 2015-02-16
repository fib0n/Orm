package hh.homework.transactionexample;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import hh.homework.transactionexample.clubs.Club;
import hh.homework.transactionexample.clubs.ClubJDBCDAO;
import hh.homework.transactionexample.common.EntityDAO;
import hh.homework.transactionexample.players.Player;
import hh.homework.transactionexample.players.PlayerHibernateDAO;
import org.hibernate.SessionFactory;
import org.postgresql.ds.PGSimpleDataSource;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by fib on 15/02/15.
 */
public class IocModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<EntityDAO<Club>>() {
        }).to(new TypeLiteral<ClubJDBCDAO>() {
        });
        bind(new TypeLiteral<EntityDAO<Player>>() {
        }).to(new TypeLiteral<PlayerHibernateDAO>() {
        });
    }

    @Provides
    @Singleton
    SessionFactory provideSessionFactory() {
        return HibernateUtils
                .buildSessionFactory(HibernateUtils.getProdConfig());
    }

    @Provides
    @Singleton
    DataSource provideDataSource() throws SQLException {
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(DatabaseConfig.url);
        dataSource.setUser(DatabaseConfig.user);
        dataSource.setPassword(DatabaseConfig.password);
        return dataSource;
    }
}
