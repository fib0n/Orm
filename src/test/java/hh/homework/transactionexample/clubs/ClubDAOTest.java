package hh.homework.transactionexample.clubs;

import hh.homework.transactionexample.ResourceUtils;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.Assert.*;


public class ClubDAOTest {

    private static ClubDAO dao;

    @BeforeClass
    public static void init() throws Exception {
        final String dbPath = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"; //keep connect
        try (final Connection conn = DriverManager.getConnection(dbPath);
             final Statement stat = conn.createStatement()) {
            stat.execute(ResourceUtils.read("schema.sql"));
            stat.execute(ResourceUtils.read("data.sql"));

            final JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setURL(dbPath);
            dao = new ClubDAO(dataSource);
        }
    }

    @Test
    public void testGet() throws Exception {
        final int id = 1;
        final Optional<Club> clubMaybe = dao.get(id);
        assertEquals(clubMaybe
                .orElseThrow(() -> new Exception(String.format("no club with id=%d", id))).getId(), id);
    }

    @Test
    public void testAddOrUpdate() throws Exception {
        Club club = new Club("ЦСКА", new BigDecimal(1000));
        club = dao.addOrUpdate(club);
        assertTrue(!club.isNew());

        final String newName = "Динамо";
        final Club clubToUpdate = club.withName(newName);
        final Club clubUpdated = dao.addOrUpdate(clubToUpdate);
        assertEquals(clubToUpdate.getId(), clubUpdated.getId());
        assertEquals(clubUpdated.name, newName);
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
