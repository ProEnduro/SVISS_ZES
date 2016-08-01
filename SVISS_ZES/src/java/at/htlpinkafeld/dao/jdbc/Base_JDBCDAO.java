/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.Base_DAO;
import at.htlpinkafeld.dao.interf.Base_DAO_Observable;
import at.htlpinkafeld.dao.util.DAODML_Observable_Impl;
import at.htlpinkafeld.dao.util.WrappedConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Six
 * @param <T>
 */
public abstract class Base_JDBCDAO<T> extends Base_DAO_Observable<T> {

    private final String TABLE_NAME;
    private final String[] PRIMARY_KEY;
    private final String[] ALL_COLUMNS;

    private final String SQL_SELECTALL_STATEMENT;
    private final String SQL_INSERT_STATEMENT;
    private final String SQL_UPDATE_STATEMENT;
    private final String SQL_DELETE_STATEMENT;
    protected final String SQL_ORDER_BY_LINE;

    protected Base_JDBCDAO(String tableName, String[] columns, String... primary_key) {
        TABLE_NAME = tableName;
        PRIMARY_KEY = primary_key;
        ALL_COLUMNS = columns;

        SQL_ORDER_BY_LINE = createOrderByLine();

        SQL_SELECTALL_STATEMENT = createSelectAllStatement();

        SQL_INSERT_STATEMENT = createInsertStatement();

        SQL_UPDATE_STATEMENT = createUpdateStatement();

        SQL_DELETE_STATEMENT = createDeleteStatement();

    }

    private String createSelectAllStatement() {
        String sql_statement = "SELECT * FROM " + TABLE_NAME + " " + SQL_ORDER_BY_LINE;
        return sql_statement;
    }

    //Used to dynamically create a sql-insert-Statement
    //usable for all Tables except n-m Relation Tables
    private String createInsertStatement() {
        String sql_statement = "INSERT INTO " + TABLE_NAME + " (";
        for (String col : ALL_COLUMNS) {
            sql_statement += col + ", ";
        }
        sql_statement = sql_statement.substring(0, sql_statement.length() - 2) + ") VALUES (";
        for (int i = ALL_COLUMNS.length; i > 0; i--) {
            sql_statement += "?, ";
        }
        sql_statement = sql_statement.substring(0, sql_statement.length() - 2) + ")";
        return sql_statement;
    }

    //Used to dynamically create a sql-update-Statement
    private String createUpdateStatement() {
        String sql_statement = "UPDATE " + TABLE_NAME + " SET ";
        for (String col : ALL_COLUMNS) {
            boolean colIsPK = false;
            for (String pk : PRIMARY_KEY) {
                if (pk.contentEquals(col)) {
                    colIsPK = true;
                }
            }
            if (!colIsPK) {
                sql_statement += col + " = ?, ";
            }
        }
        sql_statement = sql_statement.substring(0, sql_statement.length() - 2) + " WHERE ";
        for (String pk : PRIMARY_KEY) {
            sql_statement += pk + "= ? AND ";
        }
        sql_statement = sql_statement.substring(0, sql_statement.length() - 4);

        return sql_statement;
    }

    //Used to dynamically create a sql-update-Statement
    //usable for all Tables except n-m Relation Tables
    private String createDeleteStatement() {
        String sql_statement = "DELETE FROM " + TABLE_NAME + " WHERE ";
        for (String pk : PRIMARY_KEY) {
            sql_statement += pk + "= ? AND ";
        }
        sql_statement = sql_statement.substring(0, sql_statement.length() - 4);

        return sql_statement;
    }

    //Used to dynamically create the order by clause for selects
    //Sorts in the order of the PRIMARY_KEY array
    private String createOrderByLine() {
        String sql_statement = "ORDER BY ";
        for (String pk : PRIMARY_KEY) {
            sql_statement += pk + ", ";
        }
        return sql_statement.substring(0, sql_statement.length() - 2);
    }

    @Override
    public List<T> getList() {
        List<T> entityList = new ArrayList<>();

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery(SQL_SELECTALL_STATEMENT)) {

            while (rs.next()) {
                entityList.add(getEntityFromResultSet(rs));

            }
        } catch (SQLException ex) {
            Logger.getLogger(Base_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return entityList;
    }

    @Override
    public void insert(T o) {

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                PreparedStatement stmt = con.getConn().prepareStatement(SQL_INSERT_STATEMENT, Statement.RETURN_GENERATED_KEYS)) {

            Map<String, Object> entityMap = entityToMap(o);
            int i = 1;
            for (String col : ALL_COLUMNS) {
                if (entityMap.get(col).toString().contentEquals("-1")) {
                    stmt.setNull(i, Types.INTEGER);
                } else {
                    stmt.setObject(i, entityMap.get(col));
                }
                i++;
            }

            stmt.executeUpdate();
            updateEntityWithAutoKeys(stmt.getGeneratedKeys(), o);

            notifyObservers();
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(T o) {
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                PreparedStatement stmt = con.getConn().prepareStatement(SQL_UPDATE_STATEMENT);) {

            Map<String, Object> entityMap = entityToMap(o);
            int i = 1;
            for (String col : ALL_COLUMNS) {
                boolean colIsPK = false;
                for (String pk : PRIMARY_KEY) {
                    if (pk.contentEquals(col)) {
                        colIsPK = true;
                    }
                }
                if (!colIsPK) {
                    stmt.setObject(i, entityMap.get(col));
                    i++;
                }
            }

            for (String pk : PRIMARY_KEY) {
                stmt.setObject(i, entityMap.get(pk));
                i++;
            }
            stmt.executeUpdate();

            notifyObservers();
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(T o) {
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                PreparedStatement stmt = con.getConn().prepareStatement(SQL_DELETE_STATEMENT);) {

            Map<String, Object> entityMap = entityToMap(o);
            int i = 1;
            for (String pk : PRIMARY_KEY) {
                stmt.setObject(i, entityMap.get(pk));
                i++;
            }

            stmt.executeUpdate();

            notifyObservers();
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected abstract Map<String, Object> entityToMap(T entity);

    protected abstract T getEntityFromResultSet(ResultSet rs);

    protected abstract void updateEntityWithAutoKeys(ResultSet rs, T entity);
}
