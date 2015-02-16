package hh.homework.transactionexample;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by fib on 15/02/15.
 */
public class IocTestModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    private static Configuration getTestConfig() {
        return HibernateUtils
                .getConfiguration()
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
    }

    @Provides
    @Singleton
    SessionFactory provideSessionFactory() {
        return HibernateUtils
                .buildSessionFactory(getTestConfig());
    }

    @Provides
    @Singleton
    DataSource provideDataSource() throws SQLException, IOException, URISyntaxException {
        final String dbPath = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"; //keep connect
        try (final Connection conn = DriverManager.getConnection(dbPath);
             final Statement stat = conn.createStatement()) {
            stat.execute(ResourceUtils.read("schema.sql"));
            stat.execute(ResourceUtils.read("data.sql"));

            final JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setURL(dbPath);
            return dataSource;
        }
    }
}
