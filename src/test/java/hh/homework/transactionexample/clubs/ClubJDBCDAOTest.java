package hh.homework.transactionexample.clubs;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import hh.homework.transactionexample.IocModule;
import hh.homework.transactionexample.IocTestModule;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;


public class ClubJDBCDAOTest {

    private static ClubJDBCDAO dao;

    @BeforeClass
    public static void init() throws Exception {
        final Injector injector = Guice.createInjector(Modules.override(new IocModule()).with(new IocTestModule()));
        dao = injector.getInstance(ClubJDBCDAO.class);
    }

    @Test
    public void testGet() throws Exception {
        final int id = 1;
        final Optional<Club> clubMaybe = dao.get(id);
        assertEquals(clubMaybe
                .orElseThrow(() -> new Exception(String.format("no club with id=%d", id))).getId(), id);
    }

    @Test
    public void testInsertAndUpdate() throws Exception {
        Club club = new Club("ЦСКА", new BigDecimal(1000));
        club = dao.insert(club);
        assertTrue(club.getId() > 0);

        final String newName = "Динамо";
        final Club clubToUpdate = club.withName(newName);
        final int id = clubToUpdate.getId();
        dao.update(clubToUpdate);
        assertEquals(clubToUpdate.getId(), id);
        assertEquals(clubToUpdate.name, newName);
    }

    @Test
    public void testDelete() throws Exception {
        final int id = 1;
        final Optional<Club> clubMaybe = dao.get(id);
        assertTrue(clubMaybe.isPresent());
        dao.delete(id);
        final Optional<Club> deletedClubMaybe = dao.get(id);
        assertFalse(deletedClubMaybe.isPresent());
    }
}
