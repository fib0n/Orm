package hh.homework.transactionexample;

import com.google.inject.Guice;
import com.google.inject.Injector;
import hh.homework.transactionexample.clubs.ClubService;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Created by fib on 04/01/15.
 */
class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) throws Exception {
        log.info("app started...");

        final Injector injector = Guice.createInjector(new IocModule());
        final SessionFactory sessionFactory = injector.getInstance(SessionFactory.class);

        final int playerId = 4;
        final int clubCustomerId = 1;

        MDC.put("playerId", Integer.toString(playerId));
        MDC.put("clubCustomerId", Integer.toString(clubCustomerId));

        try {
            if (args.length > 0 && "reinit".equals(args[0])) {
                log.info("reinit db...");
                HibernateUtils.initDb(sessionFactory.getCurrentSession());
            }
            final ClubService clubService = injector.getInstance(ClubService.class);
            clubService.SellPlayer(playerId, clubCustomerId);
            log.info("игрок успешно продан");
        } catch (Exception e) {
            log.error("ошибка при продаже игрока", e);
        } finally {
            log.info("app finished...");
            MDC.clear();
            sessionFactory.close();
        }
    }
}
