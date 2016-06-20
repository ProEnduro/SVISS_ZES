/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.AbsenceType_DAO;
import at.htlpinkafeld.dao.util.InvalidDAOFactoryTypeException;
import at.htlpinkafeld.pojo.AbsenceType;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author Martin Six
 */
public class AbsenceType_JDBCDAOTest {

    AbsenceType_DAO absenceType_DAO;

    public AbsenceType_JDBCDAOTest() throws InvalidDAOFactoryTypeException {
        absenceType_DAO = JDBCDAOFactory.getDAOFactory().getAbsenceTypeDAO();
        ConnectionManager.setDebugInstance(true);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
// rcarver - setup the jndi context and the datasource
        try {
            // Create initial context
            /*System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.apache.naming.java.javaURLContextFactory");
            System.setProperty(Context.URL_PKG_PREFIXES,
                    "org.apache.naming");
            InitialContext ic = new javax.naming.InitialContext();

            ic.createSubcontext("java:");
            ic.createSubcontext("java:/comp");
            ic.createSubcontext("java:/comp/env");
            ic.createSubcontext("java:/comp/env/jdbc");

            // Construct DataSource
            MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
            ds.setURL("jdbc:mysql://localhost:3306/zes_sviss?zeroDateTimeBehavior=convertToNull");
            ds.setUser("root");
            ds.setPassword("admin");

            ic.bind("java:/comp/env/jdbc/zes_sviss", ds);*/

        } catch (Exception ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        List<AbsenceType> expResult = absenceType_DAO.getList();
        AbsenceType at = new AbsenceType("TestAbsence");
        expResult.add(at);
        absenceType_DAO.insert(at);
        List result = absenceType_DAO.getList();
        assertArrayEquals(expResult.toArray(), result.toArray());
    }

    /**
     * Test of update method, of class Base_JDBCDAO.
     */
    @Test
    public void testUpdateAndGetAbsenceTypeByID() {
        AbsenceType at = absenceType_DAO.getAbsenceTypeByID(1);
        assertNotEquals("TestAbsence", at.getAbsenceName());
        at.setAbsenceName("TestAbsence");
        absenceType_DAO.update(at);
        AbsenceType result = absenceType_DAO.getAbsenceTypeByID(1);
        assertEquals(at.getAbsenceName(), result.getAbsenceName());
    }

    /**
     * Test of insert and delete method, of class Base_JDBCDAO.
     */
    @Test
    public void testInsertAndDelete() {
        AbsenceType at = new AbsenceType("TestAbsence");
        absenceType_DAO.insert(at);
        List<AbsenceType> result = absenceType_DAO.getList();
        assertTrue(result.contains(at));
        absenceType_DAO.delete(at);
        result = absenceType_DAO.getList();
        assertFalse(result.contains(at));
    }
}
