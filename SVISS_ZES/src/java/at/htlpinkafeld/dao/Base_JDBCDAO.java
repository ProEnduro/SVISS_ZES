/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Six
 * @param <T>
 */
public abstract class Base_JDBCDAO<T> implements Base_DAO<T> {

    private final String TABLE_NAME;
    private final String[] PRIMARY_KEY;
    private final String[] ALL_COLUMNS;

    private static String SQL_SELECTALL_STATEMENT;
    private static String SQL_INSERT_STATEMENT;
    private static String SQL_UPDATE_STATEMENT;
    private static String SQL_DELETE_STATEMENT;

    public Base_JDBCDAO(String tableName, String[] primary_key, String[] columns) {
        TABLE_NAME = tableName;
        PRIMARY_KEY = primary_key;
        ALL_COLUMNS = columns;

        SQL_SELECTALL_STATEMENT = "SELECT * FROM " + TABLE_NAME;

        SQL_INSERT_STATEMENT = "INSERT (";
        for (String col : ALL_COLUMNS) {
            SQL_INSERT_STATEMENT += col + ", ";
        }
        SQL_INSERT_STATEMENT = SQL_INSERT_STATEMENT.substring(0, SQL_INSERT_STATEMENT.length() - 1) + ") VALUES (";
        for (int i = ALL_COLUMNS.length; i > 0; i--) {
            SQL_INSERT_STATEMENT += "?, ";
        }
        SQL_INSERT_STATEMENT = SQL_INSERT_STATEMENT.substring(0, SQL_INSERT_STATEMENT.length() - 1) + ");";

        SQL_UPDATE_STATEMENT = "UPDATE " + TABLE_NAME + " SET ";
        for (String col : ALL_COLUMNS) {
            boolean colIsPK = false;
            for (String pk : PRIMARY_KEY) {
                if (pk.contentEquals(col)) {
                    colIsPK = true;
                }
            }
            if (!colIsPK) {
                SQL_UPDATE_STATEMENT += col + " = ?, ";
            }
        }
        SQL_UPDATE_STATEMENT = SQL_UPDATE_STATEMENT.substring(0, SQL_UPDATE_STATEMENT.length() - 1) + " WHERE ";
        for (String pk : PRIMARY_KEY) {
            SQL_UPDATE_STATEMENT += pk + "= ? AND ";
        }
        SQL_UPDATE_STATEMENT = SQL_UPDATE_STATEMENT.substring(0, SQL_UPDATE_STATEMENT.length() - 4) + ";";

        SQL_DELETE_STATEMENT = "DELETE FROM " + TABLE_NAME + " WHERE ";
        for (String pk : PRIMARY_KEY) {
            SQL_DELETE_STATEMENT += pk + "= ? AND ";
        }
        SQL_DELETE_STATEMENT = SQL_DELETE_STATEMENT.substring(0, SQL_DELETE_STATEMENT.length() - 4) + ";";
    }

    @Override
    public List<T> getList() {
        List<T> entityList = new ArrayList<>();

        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(SQL_SELECTALL_STATEMENT)) {

            while (rs.next()) {
                T entity = createEntity();
                resultSetToEntity(rs, entity);
                entityList.add(entity);

            }
        } catch (SQLException ex) {
            Logger.getLogger(Base_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return entityList;
    }

    @Override
    public void insert(T o) {

        try (Connection con = ConnectionManager.getInstance().getConnection();
                PreparedStatement stmt = con.prepareStatement(SQL_INSERT_STATEMENT, Statement.RETURN_GENERATED_KEYS)) {

            Map<String, Object> entityMap = entityToMap(o);
            int i = 1;
            for (String col : ALL_COLUMNS) {
                stmt.setObject(i, entityMap.get(col));
                i++;
            }

            stmt.executeUpdate();
            updateEntityWithAutoKeys(stmt.getGeneratedKeys(), o);
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(T o) {
        try (Connection con = ConnectionManager.getInstance().getConnection();
                PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_STATEMENT);) {

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

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(T o) {
        try (Connection con = ConnectionManager.getInstance().getConnection();
                PreparedStatement stmt = con.prepareStatement(SQL_DELETE_STATEMENT);) {

            Map<String, Object> entityMap = entityToMap(o);
            int i = 1;
            for (String pk : PRIMARY_KEY) {
                stmt.setObject(i, entityMap.get(pk));
                i++;
            }

            stmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected abstract Map<String, Object> entityToMap(T entity);

    protected abstract void resultSetToEntity(ResultSet rs, T entity);

    protected abstract void updateEntityWithAutoKeys(ResultSet rs, T entity);

    protected abstract T createEntity();
}
