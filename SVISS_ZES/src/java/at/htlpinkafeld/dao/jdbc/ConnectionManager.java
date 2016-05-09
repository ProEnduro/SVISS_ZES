/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

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

    protected ConnectionManager() throws SQLException {
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

}