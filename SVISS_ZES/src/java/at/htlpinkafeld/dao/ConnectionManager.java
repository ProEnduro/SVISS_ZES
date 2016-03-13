/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao;

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

    private static final String DATASOURCE = "jdbc/testdb";

    private static ConnectionManager conM = null;
    private DataSource ds;
    private User_DAO userDAO;

    private ConnectionManager() throws SQLException {
        userDAO=new User_JDBCDAO();
        Context ctx;
        try {
            ctx = new javax.naming.InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/" + DATASOURCE);
        } catch (NamingException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static synchronized ConnectionManager getInstance() throws SQLException {
        if (conM == null) {
            conM = new ConnectionManager();
        }
        return conM;
    }

    protected Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
