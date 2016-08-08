/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserProxy;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Martin Six
 */
public class User_JDBCDAOTest {

    User_DAO user_DAO;

    public User_JDBCDAOTest() {
        user_DAO = JDBCDAOFactory.getDAOFactory().getUserDAO();
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
        List<User> expResult = user_DAO.getList();
        User user = new UserProxy(JDBCDAOFactory.getDAOFactory().getAccessLevelDAO().getAccessLevelByID(1), "TestAdminUser", 0, 100, "testAdmin", "tadmin@test.at", LocalDate.of(2016, 3, 7), "tssadmin", 38.5);
        user_DAO.insert(user);
        expResult.add(user);
        List result = user_DAO.getList();
        assertArrayEquals(expResult.toArray(), result.toArray());
    }

    /**
     * Test of update and GetByUsername method, of class Base_JDBCDAO.
     */
    @Test
    public void testUpdateAndGetUserByUsername() {
        User user = new UserProxy(JDBCDAOFactory.getDAOFactory().getAccessLevelDAO().getAccessLevelByID(1), "TestAdminUser", 0, 100, "testAdmin", "tadmin@test.at", LocalDate.of(2016, 3, 7), "tssadmin", 38.5);
        user_DAO.insert(user);
        user = user_DAO.getUserByUsername("testAdmin");
        user.setEmail("adminemailadwd");
        user_DAO.update(user);
        User result = user_DAO.getUser(user.getUserNr());
        assertEquals(user.getEmail(), result.getEmail());
    }

    /**
     * Test of insert and delete method, of class Base_JDBCDAO.
     */
    @Test
    public void testInsertAndDelete() {
        User user = new UserProxy(JDBCDAOFactory.getDAOFactory().getAccessLevelDAO().getAccessLevelByID(1), "TestAdminUser", 0, 100, "testAdmin", "tadmin@test.at", LocalDate.of(2016, 3, 7), "tssadmin", 38.5);
        user_DAO.insert(user);
        List<User> result = user_DAO.getList();
        assertTrue(result.contains(user));
        user_DAO.delete(user);
        result = user_DAO.getList();
        assertFalse(result.contains(user));
    }
}
