/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.AbsenceTypeNew;
import at.htlpinkafeld.pojo.User;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Martin Six
 */
public class Absence_JDBCDAOTest {

    Absence_DAO absence_DAO;

    public Absence_JDBCDAOTest() {
        absence_DAO = DAOFactory.getDAOFactory().getAbsenceDAO();
        ConnectionManager.setDebugInstance(true);
    }

    @AfterClass
    public static void setUpClass() throws Exception {
        ConnectionManager.setDebugInstance(false);
    }

    @After
    public void tearDown() {
        try {
            ConnectionManager.getInstance().getWrappedConnection().getConn().rollback();
        } catch (SQLException ex) {
            Logger.getLogger(Absence_JDBCDAOTest.class.getName()).log(Level.SEVERE, null, ex);
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
        Absence absence = new Absence(u, AbsenceTypeNew.MEDICAL_LEAVE, LocalDateTime.of(2016, 4, 4, 0, 0, 0), LocalDateTime.of(2016, 4, 6, 0, 0, 0), "Pest");
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
        List<Absence> expResult = absence_DAO.getAbsencesByUser(u);
        Absence absence = new Absence(u, AbsenceTypeNew.MEDICAL_LEAVE, LocalDateTime.of(2016, 4, 4, 0, 0, 0), LocalDateTime.of(2016, 4, 6, 0, 0, 0), "Pest");
        absence_DAO.insert(absence);
        expResult.add(absence);
        List result = absence_DAO.getAbsencesByUser(u);
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
        AbsenceTypeNew at = AbsenceTypeNew.MEDICAL_LEAVE;
        List<Absence> expResult = absence_DAO.getAbsencesByAbsenceType_User(at, u);
        Absence absence = new Absence(u, at, LocalDateTime.of(2016, 4, 4, 0, 0, 0), LocalDateTime.of(2016, 4, 6, 0, 0, 0), "Pest");
        absence_DAO.insert(absence);
        expResult.add(absence);
        List<Absence> result = absence_DAO.getAbsencesByAbsenceType_User(at, u);
        assertArrayEquals(expResult.toArray(), result.toArray());
    }

    /**
     * Test of update and GetByUsername method, of class Base_JDBCDAO.
     */
    @Test
    public void test_Insert_Update_GetByAbsenceType() {
        User u = DAOFactory.getDAOFactory().getUserDAO().getUser(1);
        AbsenceTypeNew at = AbsenceTypeNew.MEDICAL_LEAVE;
        Absence absence = new Absence(u, at, LocalDateTime.of(2016, 4, 4, 0, 0, 0), LocalDateTime.of(2016, 4, 6, 0, 0, 0));
        absence_DAO.insert(absence);
        List<Absence> resultList = absence_DAO.getAbsencesByAbsenceType(at);
        Absence result = resultList.get(resultList.indexOf(absence));

        absence.setAcknowledged(true);
        absence.setEndTime(LocalDateTime.of(2016, 4, 10, 0, 0, 0));
        assertNotEquals(absence.isAcknowledged(), result.isAcknowledged());
        assertNotEquals(absence.getEndTime(), result.getEndTime());

        absence_DAO.update(absence);
        resultList = absence_DAO.getAbsencesByAbsenceType(at);
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
        Absence absence = new Absence(u, AbsenceTypeNew.MEDICAL_LEAVE, LocalDateTime.of(2016, 4, 4, 0, 0, 0), LocalDateTime.of(2016, 4, 6, 0, 0, 0), "Pest");
        absence_DAO.insert(absence);
        List<Absence> result = absence_DAO.getList();
        assertTrue(result.contains(absence));
        absence_DAO.delete(absence);
        result = absence_DAO.getList();
        assertFalse(result.contains(absence));
    }

    /**
     * Test of getAbsencesByAcknowledgment method, of class Absence_JDBCDAO.
     */
    @Test
    public void testGetAbsencesByAcknowledgment() {
        User u = DAOFactory.getDAOFactory().getUserDAO().getUser(1);
        Absence absence = new Absence(u, AbsenceTypeNew.MEDICAL_LEAVE, LocalDateTime.of(2016, 4, 4, 0, 0, 0), LocalDateTime.of(2016, 4, 6, 0, 0, 0), "Pest");
        absence_DAO.insert(absence);
        List<Absence> result = absence_DAO.getAbsencesByAcknowledgment(false);
        assertTrue(result.contains(absence));
        absence.setAcknowledged(true);
        absence_DAO.update(absence);
        result = absence_DAO.getAbsencesByAcknowledgment(true);
        assertTrue(result.contains(absence));
    }

    /**
     * Test of getAbsencesByUser_Acknowledgment method, of class
     * Absence_JDBCDAO.
     */
    @Test
    public void testGetAbsencesByUserAndAcknowledgment() {
        User u = DAOFactory.getDAOFactory().getUserDAO().getUser(1);
        Absence absence = new Absence(u, AbsenceTypeNew.MEDICAL_LEAVE, LocalDateTime.of(2016, 4, 4, 0, 0, 0), LocalDateTime.of(2016, 4, 6, 0, 0, 0), "Pest");
        absence_DAO.insert(absence);
        List<Absence> result = absence_DAO.getAbsencesByUser_Acknowledgment(u, false);
        assertTrue(result.contains(absence));
        absence.setAcknowledged(true);
        absence_DAO.update(absence);
        result = absence_DAO.getAbsencesByUser_Acknowledgment(u, true);
        assertTrue(result.contains(absence));
    }

}
