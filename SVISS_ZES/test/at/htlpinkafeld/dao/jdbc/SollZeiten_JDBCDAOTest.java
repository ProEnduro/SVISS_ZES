/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import java.sql.SQLException;
import java.util.HashMap;
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
public class SollZeiten_JDBCDAOTest {

    SollZeiten_DAO sollZeiten_DAO;

    public SollZeiten_JDBCDAOTest() {
        sollZeiten_DAO = JDBCDAOFactory.getDAOFactory().getSollZeitenDAO();
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
            Logger.getLogger(SollZeiten_JDBCDAOTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getList method, of class Base_JDBCDAO.
     *
     * @throws java.sql.SQLException
     */
    @Test
    public void testInsertAndGetList() throws SQLException {
        List<SollZeit> expResult = sollZeiten_DAO.getList();

        SollZeit sz = new SollZeit(DAOFactory.getDAOFactory().getUserDAO().getUser(1), new HashMap(), new HashMap());
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
        List<SollZeit> expResult = sollZeiten_DAO.getSollZeitenByUser(u);
        SollZeit sz = new SollZeit(DAOFactory.getDAOFactory().getUserDAO().getUser(1), null, null);
        sollZeiten_DAO.insert(sz);
        expResult.add(sz);
        List result = sollZeiten_DAO.getSollZeitenByUser(u);
        assertArrayEquals(expResult.toArray(), result.toArray());
    }

    /**
     * Test of update and GetByUsername method, of class Base_JDBCDAO.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testUpdate() {
        SollZeit sz = new SollZeit(DAOFactory.getDAOFactory().getUserDAO().getUser(1), null, null);
        sollZeiten_DAO.insert(sz);

        sollZeiten_DAO.update(sz);
    }

    /**
     * Test of insert and delete method, of class Base_JDBCDAO.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testInsertAndDelete() throws InterruptedException {
        SollZeit sz = new SollZeit(DAOFactory.getDAOFactory().getUserDAO().getUser(1), null, null);
        sollZeiten_DAO.insert(sz);
        List<SollZeit> result = sollZeiten_DAO.getList();
        assertTrue(result.contains(sz));
        sollZeiten_DAO.delete(sz);
    }

}
