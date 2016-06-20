/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.WorkTime_DAO;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import java.sql.SQLException;
import java.util.GregorianCalendar;
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
public class WorkTime_JDBCDAOTest {

    WorkTime_DAO workTime_DAO;

    public WorkTime_JDBCDAOTest() {
        workTime_DAO = JDBCDAOFactory.getDAOFactory().getWorkTimeDAO();
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
        List<WorkTime> expResult = workTime_DAO.getList();
        WorkTime wt = new WorkTime(DAOFactory.getDAOFactory().getUserDAO().getUser(1), new GregorianCalendar(2016, 3, 14, 8, 0).getTime(), new GregorianCalendar(2016, 3, 14, 16, 30).getTime(), 30, "Start Test", "End Test");
        workTime_DAO.insert(wt);
        expResult.add(wt);
        List result = workTime_DAO.getList();
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
        List<WorkTime> expResult = workTime_DAO.getWorkTimesByUser(u);
        WorkTime wt = new WorkTime(u, new GregorianCalendar(2016, 3, 14, 8, 0).getTime(), new GregorianCalendar(2016, 3, 14, 16, 30).getTime(), 30, "Start Test", "End Test");
        workTime_DAO.insert(wt);
        expResult.add(wt);
        List result = workTime_DAO.getWorkTimesByUser(u);
        assertArrayEquals(expResult.toArray(), result.toArray());
    }

    /**
     * Test of update and GetByUsername method, of class Base_JDBCDAO.
     */
    @Test
    public void testInsertAndUpdate() {
        WorkTime wt = new WorkTime(DAOFactory.getDAOFactory().getUserDAO().getUser(1), new GregorianCalendar(2016, 3, 14, 8, 0).getTime(), new GregorianCalendar(2016, 3, 14, 16, 30).getTime(), 30, "Start Test", "End Test");
        workTime_DAO.insert(wt);
        wt.setBreakTime(50);
        wt.setEndComment("Updated Test Test");
        workTime_DAO.update(wt);
        List<WorkTime> resultList = workTime_DAO.getWorkTimesByUser(wt.getUser());
        WorkTime result = resultList.get(resultList.indexOf(wt));
        assertEquals(wt.getBreakTime(), result.getBreakTime());
        assertEquals(wt.getEndComment(), result.getEndComment());
    }

    /**
     * Test of insert and delete method, of class Base_JDBCDAO.
     */
    @Test
    public void testInsertAndDelete() {
        WorkTime wt = new WorkTime(DAOFactory.getDAOFactory().getUserDAO().getUser(1), new GregorianCalendar(2016, 3, 14, 8, 0).getTime(), new GregorianCalendar(2016, 3, 14, 16, 30).getTime(), 30, "Start Test", "End Test");
        workTime_DAO.insert(wt);
        List<WorkTime> result = workTime_DAO.getList();
        assertTrue(result.contains(wt));
        workTime_DAO.delete(wt);
        result = workTime_DAO.getList();
        assertFalse(result.contains(wt));
    }

}
