/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.pojo.SollZeiten;
import at.htlpinkafeld.pojo.User;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Martin Six
 */
public class SollZeiten_JDBCDAOTest {

    SollZeiten_DAO sollZeiten_DAO;

    public SollZeiten_JDBCDAOTest() {
        sollZeiten_DAO = JDBCDAOFactory.getDAOFactory().getSollZeitenDAO();
        ConnectionManager.setDebugInstance(true);
    }

    @After
    public void tearDown() {
        try {
            ConnectionManager.getInstance().getWrappedConnection().getConn().rollback();
        } catch (SQLException ex) {
            Logger.getLogger(AbsenceType_JDBCDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getList method, of class Base_JDBCDAO.
     *
     * @throws java.sql.SQLException
     */
    @Test
    public void testInsertAndGetList() throws SQLException {
        List<SollZeiten> expResult = sollZeiten_DAO.getList();
        SollZeiten sz = new SollZeiten(DayOfWeek.of(7), DAOFactory.getDAOFactory().getUserDAO().getUser(1), LocalTime.parse("08:00:00"), LocalTime.parse("16:30:00"));
        sollZeiten_DAO.insert(sz);
        expResult.add(sz);
        List result = sollZeiten_DAO.getList();
        assertTrue(expResult.containsAll(result));
        assertTrue(result.containsAll(expResult));
    }

    /**
     * Test of getWorkTimeByUser method, of class WorkTime_JDBCDAO.
     *
     * @throws java.sql.SQLException
     */
    @Test
    public void testInsertAndGetByUser() throws SQLException {
        User u = DAOFactory.getDAOFactory().getUserDAO().getUser(1);
        List<SollZeiten> expResult = sollZeiten_DAO.getSollZeitenByUser(u);
        SollZeiten sz = new SollZeiten(DayOfWeek.of(7), DAOFactory.getDAOFactory().getUserDAO().getUser(1), LocalTime.parse("08:00:00"), LocalTime.parse("16:30:00"));
        sollZeiten_DAO.insert(sz);
        expResult.add(sz);
        List result = sollZeiten_DAO.getSollZeitenByUser(u);
        assertArrayEquals(expResult.toArray(), result.toArray());
    }

    /**
     * Test of update and GetByUsername method, of class Base_JDBCDAO.
     */
    @Test
    public void testInsertAndUpdate() {
        SollZeiten sz = new SollZeiten(DayOfWeek.of(7), DAOFactory.getDAOFactory().getUserDAO().getUser(1), LocalTime.parse("08:00:00"), LocalTime.parse("16:30:00"));
        sollZeiten_DAO.insert(sz);
        List<SollZeiten> resultList = sollZeiten_DAO.getSollZeitenByUser(sz.getUser());
        SollZeiten result = resultList.get(resultList.indexOf(sz));

        sz.setSollEndTime(LocalTime.parse("09:00:00"));
        sz.setSollStartTime(LocalTime.parse("17:30:00"));
        assertNotEquals(sz.getSollStartTime(), result.getSollStartTime());
        assertNotEquals(sz.getSollEndTime(), result.getSollEndTime());

        sollZeiten_DAO.update(sz);
        resultList = sollZeiten_DAO.getSollZeitenByUser(sz.getUser());
        result = resultList.get(resultList.indexOf(sz));
        assertEquals(sz.getSollStartTime(), result.getSollStartTime());
        assertEquals(sz.getSollEndTime(), result.getSollEndTime());
    }

    /**
     * Test of insert and delete method, of class Base_JDBCDAO.
     */
    @Test
    public void testInsertAndDelete() throws InterruptedException {
        SollZeiten sz = new SollZeiten(DayOfWeek.of(7), DAOFactory.getDAOFactory().getUserDAO().getUser(1), LocalTime.parse("08:00:00"), LocalTime.parse("16:30:00"));
        sollZeiten_DAO.insert(sz);
        List<SollZeiten> result = sollZeiten_DAO.getList();
        assertTrue(result.contains(sz));
        sollZeiten_DAO.delete(sz);
        result = sollZeiten_DAO.getList();
        assertFalse(result.contains(sz));
    }

}
