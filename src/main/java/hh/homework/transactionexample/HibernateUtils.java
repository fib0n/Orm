package hh.homework.transactionexample;

import hh.homework.transactionexample.players.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by fib on 04/01/15.
 */
public class HibernateUtils {

    private HibernateUtils() {
    }

    private static Configuration getConfiguration() {
        return new Configuration().addAnnotatedClass(Player.class);
    }

    public static Configuration getProdConfig() {
        return getConfiguration()
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", DatabaseConfig.url)
                .setProperty("hibernate.connection.username", DatabaseConfig.user)
                .setProperty("hibernate.connection.password", DatabaseConfig.password);
    }

    public static Configuration getTestConfig() {
        return getConfiguration()
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:test");
    }

    public static SessionFactory buildSessionFactory(final Configuration configuration) {
        final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    public static void initDb(final Session session) throws IOException, URISyntaxException {
        final Transaction transaction = session.beginTransaction();
        try {
            session.createSQLQuery(ResourceUtils.read("schema.sql")).executeUpdate();
            session.createSQLQuery(ResourceUtils.read("data.sql")).executeUpdate();
            transaction.commit();
        } catch (IOException | URISyntaxException e) {
            transaction.rollback();
            throw e;
        }
    }
}
