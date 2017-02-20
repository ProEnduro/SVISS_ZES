/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.util.DAOException;
import at.htlpinkafeld.dao.util.WrappedConnection;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
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
        Context ctx;
        if (DAOFactory.getDAOFactory() instanceof JDBCDAOFactory) {
            MysqlDataSource mysqlds = new MysqlDataSource();
//                ctx = new javax.naming.InitialContext();
//                ds = (DataSource) ctx.lookup("java:comp/env/" + DATASOURCE);
            mysqlds.setURL("jdbc:mysql://localhost:3306/ZES_SVISS");
            mysqlds.setUser("root");
            mysqlds.setPassword("Burgenland2016#");
            ds = mysqlds;
            if (test) {
                testConnection = new WrappedConnection(ds.getConnection(), false);
                testConnection.getConn().setAutoCommit(false);
            }
        }

    }

    /**
     * Only to be used for testing purposes. Causes the programm to only have a
     * single connection, without auto-commits
     *
     * @param testing
     */
    public static synchronized void setDebugInstance(boolean testing) {
        test = testing;
    }

    public static synchronized void rollback() {
        if (test) {
            try {
                ConnectionManager cm = getInstance();
                if (cm.getWrappedConnection() != null) {
                    cm.getWrappedConnection().getConn().rollback();
                }
            } catch (SQLException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
            throw new DAOException(ex);
        }

        return retVal;
    }

}
