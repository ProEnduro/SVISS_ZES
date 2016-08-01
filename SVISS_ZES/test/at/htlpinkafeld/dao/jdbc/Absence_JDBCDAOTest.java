/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.AbsenceType_DAO;
import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.AbsenceType;
import at.htlpinkafeld.pojo.User;
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
public class Absence_JDBCDAOTest {

    Absence_DAO absence_DAO;
    AbsenceType_DAO absenceType_DAO;

    public Absence_JDBCDAOTest() {
        absence_DAO = DAOFactory.getDAOFactory().getAbsenceDAO();
        absenceType_DAO = DAOFactory.getDAOFactory().getAbsenceTypeDAO();
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
        List<Absence> expResult = absence_DAO.getList();
        User u = DAOFactory.getDAOFactory().getUserDAO().getUser(1);
        Absence absence = new Absence(u, absenceType_DAO.getAbsenceTypeByID(1), new GregorianCalendar(2016, 4, 4).getTime(), new GregorianCalendar(2016, 4, 6).getTime(), "Pest");
        absence_DAO.insert(absence);
        expResult.add(absence);
        List<Absence> result = absence_DAO.getList();
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
        List<Absence> expResult = absence_DAO.getAbsenceByUser(u);
        Absence absence = new Absence(u, absenceType_DAO.getAbsenceTypeByID(1), new GregorianCalendar(2016, 4, 4).getTime(), new GregorianCalendar(2016, 4, 6).getTime(), "Pest");
        absence_DAO.insert(absence);
        expResult.add(absence);
        List result = absence_DAO.getAbsenceByUser(u);
        assertArrayEquals(expResult.toArray(), result.toArray());
    }

    /**
     * Test of getWorkTimeByUser method, of class WorkTime_JDBCDAO.
     *
     * @throws java.sql.SQLException
     */
    @Test
    public void testInsertAndGetByAbsenceTypeAndUser() throws SQLException {
        User u = DAOFactory.getDAOFactory().getUserDAO().getUser(1);
        AbsenceType at = absenceType_DAO.getAbsenceTypeByID(1);
        List<Absence> expResult = absence_DAO.getAbsenceByAbsenceTypeAndUser(at, u);
        Absence absence = new Absence(u, at, new GregorianCalendar(2016, 4, 4).getTime(), new GregorianCalendar(2016, 4, 6).getTime(), "Pest");
        absence_DAO.insert(absence);
        expResult.add(absence);
        List result = absence_DAO.getAbsenceByUser(u);
        assertArrayEquals(expResult.toArray(), result.toArray());
    }

    /**
     * Test of update and GetByUsername method, of class Base_JDBCDAO.
     */
    @Test
    public void test_Insert_Update_GetByAbsenceType() {
        User u = DAOFactory.getDAOFactory().getUserDAO().getUser(2);
        AbsenceType at = absenceType_DAO.getAbsenceTypeByID(3);
        Absence absence = new Absence(u, at, new GregorianCalendar(2016, 4, 4).getTime(), new GregorianCalendar(2016, 4, 6).getTime());
        absence_DAO.insert(absence);
        List<Absence> resultList = absence_DAO.getAbsenceByAbsenceType(at);
        Absence result = resultList.get(resultList.indexOf(absence));

        absence.setAcknowledged(true);
        absence.setEndTime(new GregorianCalendar(2016, 4, 10).getTime());
        assertNotEquals(absence.isAcknowledged(), result.isAcknowledged());
        assertNotEquals(absence.getEndTime(), result.getEndTime());

        absence_DAO.update(absence);
        resultList = absence_DAO.getAbsenceByAbsenceType(at);
        result = resultList.get(resultList.indexOf(absence));
        assertEquals(absence.isAcknowledged(), result.isAcknowledged());
        assertEquals(absence.getEndTime(), result.getEndTime());
    }

    /**
     * Test of insert and delete method, of class Base_JDBCDAO.
     */
    @Test
    public void testInsertAndDelete() {
        User u = DAOFactory.getDAOFactory().getUserDAO().getUser(1);
        Absence absence = new Absence(u, absenceType_DAO.getAbsenceTypeByID(1), new GregorianCalendar(2016, 4, 4).getTime(), new GregorianCalendar(2016, 4, 6).getTime(), "Pest");
        absence_DAO.insert(absence);
        List<Absence> result = absence_DAO.getList();
        assertTrue(result.contains(absence));
        absence_DAO.delete(absence);
        result = absence_DAO.getList();
        assertFalse(result.contains(absence));
    }

}
