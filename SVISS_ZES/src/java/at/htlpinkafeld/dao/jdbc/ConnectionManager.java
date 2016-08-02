/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.util.WrappedConnection;
import java.sql.DriverManager;
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

    private static boolean test = false;
    private WrappedConnection testConnection = null;

    protected ConnectionManager() throws SQLException {
        try {
            if (!test) {
                Context ctx;

                ctx = new javax.naming.InitialContext();
                ds = (DataSource) ctx.lookup("java:comp/env/" + DATASOURCE);
            } else {
                testConnection = new WrappedConnection(DriverManager.getConnection("jdbc:mysql://localhost:3306/" + "zes_sviss?useSSL=false", "root", "admin"), false);
                testConnection.getConn().setAutoCommit(false);
            }
        } catch (NamingException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Only to be used for testing purposes. Causes the programm to only have a
     * single connection, without auto-commits
     *
     * @param testing
     */
    protected static synchronized void setDebugInstance(boolean testing) {
        test = testing;
    }

    protected static synchronized ConnectionManager getInstance() throws SQLException {
        if (conM == null) {
            conM = new ConnectionManager();
        }
        return conM;
    }

    protected WrappedConnection getWrappedConnection() {
        WrappedConnection retVal = null;
        try {
            if (test) {
                retVal = testConnection;
            } else {
                retVal = new WrappedConnection(ds.getConnection(), true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
    }

}
