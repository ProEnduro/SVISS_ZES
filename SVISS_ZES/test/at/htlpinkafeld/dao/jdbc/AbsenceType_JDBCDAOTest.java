/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.util.InvalidDAOFactoryTypeException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

/**
 *
 * @author Martin Six
 */
@Deprecated
public class AbsenceType_JDBCDAOTest {

    /**
     * Constructor for AbsenceType_JDBCDAOTest
     * @throws InvalidDAOFactoryTypeException is thrown if something goes wrong
     */
    public AbsenceType_JDBCDAOTest() throws InvalidDAOFactoryTypeException {
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
//        List<AbsenceType> expResult = absenceType_DAO.getList();
//        AbsenceType at = new AbsenceType("TestAbsence");
//        expResult.add(at);
//        absenceType_DAO.insert(at);
//        List result = absenceType_DAO.getList();
//        assertArrayEquals(expResult.toArray(), result.toArray());
    }

    /**
     * Test of update method, of class Base_JDBCDAO.
     */
    @Test
    public void testUpdateAndGetAbsenceTypeByID() {
//        AbsenceType at = absenceType_DAO.getAbsenceTypeByID(1);
//        assertNotEquals("TestAbsence", at.getAbsenceName());
//        at.setAbsenceName("TestAbsence");
//        absenceType_DAO.update(at);
//        AbsenceType result = absenceType_DAO.getAbsenceTypeByID(1);
//        assertEquals(at.getAbsenceName(), result.getAbsenceName());
    }

    /**
     * Test of insert and delete method, of class Base_JDBCDAO.
     */
    @Test
    public void testInsertAndDelete() {
//        AbsenceType at = new AbsenceType("TestAbsence");
//        absenceType_DAO.insert(at);
//        List<AbsenceType> result = absenceType_DAO.getList();
//        assertTrue(result.contains(at));
//        absenceType_DAO.delete(at);
//        result = absenceType_DAO.getList();
//        assertFalse(result.contains(at));
    }
}
