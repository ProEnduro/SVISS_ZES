/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Martin Six
 */
public class WrappedConnection implements AutoCloseable {

    private final Connection conn;
    private final boolean doClose;

    public WrappedConnection(Connection conn, boolean doClose) {
        this.conn = conn;
        this.doClose = doClose;
    }

    public Connection getConn() {
        return conn;
    }

    @Override
    public void close() throws SQLException {
        if (this.doClose) {
            this.conn.close();
        }
    }
}
