package hh.homework.transactionexample;

import com.google.inject.Guice;
import com.google.inject.Injector;
import hh.homework.transactionexample.clubs.ClubService;
import org.hibernate.SessionFactory;

/**
 * Created by fib on 04/01/15.
 */
class Main {

    public static void main(final String[] args) throws Exception {
        final Injector injector = Guice.createInjector(new IocModule());
        final SessionFactory sessionFactory = injector.getInstance(SessionFactory.class);

        try {
            if (args.length > 0 && "reinit".equals(args[0]))
                HibernateUtils.initDb(sessionFactory.getCurrentSession());

            final ClubService clubService = injector.getInstance(ClubService.class);

            clubService.SellPlayer(4, 1);
            System.out.println("Игрок успешно продан");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sessionFactory.close();
        }
    }
}
