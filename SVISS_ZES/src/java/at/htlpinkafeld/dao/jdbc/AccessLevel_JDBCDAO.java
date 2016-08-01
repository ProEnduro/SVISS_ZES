/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.AccessLevel_DAO;
import at.htlpinkafeld.dao.util.WrappedConnection;
import at.htlpinkafeld.pojo.AccessLevel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Six
 */
public class AccessLevel_JDBCDAO extends Base_JDBCDAO<AccessLevel> implements AccessLevel_DAO {

    public static final String ACCESSLEVELID_COL = "AccessLevelID";
    public static final String ACCESSLEVELNAME_COL = "AccessLevelName";
    private static final String PERMISSIONS_COL = "Permissions";

    public static final String TABLE_NAME = "ZESAccess";
    public static final String PRIMARY_KEY = ACCESSLEVELID_COL;
    public static final String[] ALL_COLUMNS = {ACCESSLEVELID_COL, ACCESSLEVELNAME_COL, PERMISSIONS_COL};

    protected AccessLevel_JDBCDAO() {
        super(TABLE_NAME, ALL_COLUMNS, PRIMARY_KEY);
    }

    @Override
    public AccessLevel getAccessLevelByID(int accessLevelId) {
        AccessLevel al = null;
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ACCESSLEVELID_COL + " = " + accessLevelId + " " + SQL_ORDER_BY_LINE)) {

            if (rs.next()) {
                al = new AccessLevel(rs.getInt(ACCESSLEVELID_COL), rs.getString(ACCESSLEVELNAME_COL), new LinkedList<>(Arrays.asList(rs.getString(PERMISSIONS_COL).split(";"))));

            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return al;
    }

    @Override
    protected Map<String, Object> entityToMap(AccessLevel entity
    ) {
        Map<String, Object> resMap = new HashMap<>();

        resMap.put(ACCESSLEVELID_COL, entity.getAccessLevelID());
        resMap.put(ACCESSLEVELNAME_COL, entity.getAccessLevelName());
        String serialPerm = "";
        for (String p : entity.getPermissions()) {
            serialPerm += p + ";";
        }
        resMap.put(PERMISSIONS_COL, serialPerm);
        return resMap;
    }

    @Override
    protected AccessLevel getEntityFromResultSet(ResultSet rs
    ) {
        try {
            return new AccessLevel(rs.getInt(ACCESSLEVELID_COL), rs.getString(ACCESSLEVELNAME_COL), new LinkedList<>(Arrays.asList(rs.getString(PERMISSIONS_COL).split(";"))));
        } catch (SQLException ex) {
            Logger.getLogger(AccessLevel_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected void updateEntityWithAutoKeys(ResultSet rs, AccessLevel entity) {
        try {
            rs.next();
            entity.setAccessLevelID(rs.getInt(1));
        } catch (SQLException ex) {
            Logger.getLogger(AccessLevel_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
