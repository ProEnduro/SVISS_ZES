/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao;

import at.htlpinkafeld.dao.dummy.AbsenceType_DummyDAO;
import at.htlpinkafeld.dao.dummy.Absence_DummyDAO;
import at.htlpinkafeld.dao.dummy.AccessLevel_DummyDAO;
import at.htlpinkafeld.dao.dummy.Permission_DummyDAO;
import at.htlpinkafeld.dao.dummy.SollZeiten_DummyDAO;
import at.htlpinkafeld.dao.dummy.User_DummyDAO;
import at.htlpinkafeld.dao.dummy.WorkTime_DummyDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author Martin Six
 */
public class ConnectionManager {

    private static final String DATASOURCE = "jdbc/zes_sviss";

    private static ConnectionManager conM = null;
    private DataSource ds;

    private ConnectionManager() throws SQLException {
        try {
            Context ctx;
            ctx = new javax.naming.InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/" + DATASOURCE);
        } catch (NamingException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected static synchronized ConnectionManager getInstance() throws SQLException {
        if (conM == null) {
            conM = new ConnectionManager();
        }
        return conM;
    }

    protected Connection getConnection() {
        Connection retVal = null;
        try {
            retVal = ds.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
    }

    public static User_DAO getUserDAO() {
        return new User_DummyDAO();
    }
    
    public static Permission_DAO getPermissionDAO(){
        return new Permission_DummyDAO();
    }
    
    public static AccessLevel_DAO getAccessLevelDAO(){
        return new AccessLevel_DummyDAO();
    }
    
    public static WorkTime_DAO getWorkTimeDAO(){
        return new WorkTime_DummyDAO();
    }
    
    public static SollZeiten_DAO getSollZeitenDAO(){
        return new SollZeiten_DummyDAO();
    }
    
    public static AbsenceType_DAO getAbsenceTypeDAO(){
        return new AbsenceType_DummyDAO();
    }
    
    public static Absence_DAO getAbsenceDAO(){
        return new Absence_DummyDAO();
    }
}
