package hh.homework.transactionexample;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by fib on 04/01/15.
 */
class DatabaseConfig {
    public static final String url;
    public static final String user;
    public static final String password;

    static {
        final Properties prop = getProperties();
        url = prop.getProperty("dburl");
        user = prop.getProperty("dbuser");
        password = prop.getProperty("dbpassword");
    }

    private DatabaseConfig() {
    }

    private static Properties getProperties() {
        final Properties prop = new Properties();
        final InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
        try {
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }
}
