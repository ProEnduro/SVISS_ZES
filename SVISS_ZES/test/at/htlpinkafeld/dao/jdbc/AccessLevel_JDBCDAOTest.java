/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.AccessLevel_DAO;
import at.htlpinkafeld.pojo.AccessLevel;
import java.sql.SQLException;
import java.util.LinkedList;
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
public class AccessLevel_JDBCDAOTest {

    AccessLevel_DAO accessLevel_DAO;

    public AccessLevel_JDBCDAOTest() {
        accessLevel_DAO = JDBCDAOFactory.getDAOFactory().getAccessLevelDAO();
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
        List<AccessLevel> expResult = accessLevel_DAO.getList();
        List permList = new LinkedList<>();
        permList.add("APPROVER");
        permList.add("EDIT_USERS");
        AccessLevel al = new AccessLevel("TAdmin", permList);
        expResult.add(al);
        accessLevel_DAO.insert(al);
        List result = accessLevel_DAO.getList();
        assertArrayEquals(expResult.toArray(), result.toArray());
    }

    /**
     * Test of update method, of class Base_JDBCDAO.
     */
    @Test
    public void testUpdateAndGetAccessLevelByID() {
        AccessLevel al = accessLevel_DAO.getAccessLevelByID(1);
        assertNotEquals("TestAdmin", al.getAccessLevelName());
        al.setAccessLevelName("TestAdmin");
        al.setPermissions(new LinkedList<>());
        accessLevel_DAO.update(al);
        AccessLevel result = accessLevel_DAO.getAccessLevelByID(1);
        assertEquals(al.getAccessLevelName(), result.getAccessLevelName());
    }

    /**
     * Test of insert and delete method, of class Base_JDBCDAO.
     */
    @Test
    public void testInsertAndDelete() {
        List permList = new LinkedList<>();
        permList.add("APPROVER");
        permList.add("EDIT_USERS");
        AccessLevel al = new AccessLevel("TAdmin", permList);
        accessLevel_DAO.insert(al);
        List<AccessLevel> result = accessLevel_DAO.getList();
        assertTrue(result.contains(al));
        accessLevel_DAO.delete(al);
        result = accessLevel_DAO.getList();
        assertFalse(result.contains(al));
    }

}
