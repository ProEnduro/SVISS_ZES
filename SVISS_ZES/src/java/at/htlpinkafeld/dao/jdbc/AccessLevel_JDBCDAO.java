/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.AccessLevel_DAO;
import at.htlpinkafeld.dao.interf.Permission_DAO;
import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.pojo.Permission;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
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

    public static final String TABLE_NAME = "ZESAccess";
    public static final String RELATION_TABLE_NAME = "AccessPerm";
    public static final String PRIMARY_KEY = ACCESSLEVELID_COL;
    public static final String[] ALL_COLUMNS = {ACCESSLEVELID_COL, ACCESSLEVELNAME_COL};

    protected AccessLevel_JDBCDAO() {
        super(TABLE_NAME, ALL_COLUMNS, PRIMARY_KEY);
    }

    @Override
    public void insert(AccessLevel o) {
        super.insert(o);
        insertPermissions(o);
    }

    @Override
    public void update(AccessLevel o) {
        super.update(o);

        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement()) {
            stmt.executeUpdate("DELETE FROM " + RELATION_TABLE_NAME + " WHERE" + ACCESSLEVELID_COL + " = " + o.getAccessLevelID());
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        insertPermissions(o);
    }

    private void insertPermissions(AccessLevel al) {
        try (Connection con = ConnectionManager.getInstance().getConnection();
                PreparedStatement stmt = con.prepareStatement("INSERT INTO " + RELATION_TABLE_NAME + " (" + ACCESSLEVELID_COL + ", " + Permission_JDBCDAO.PERMISSIONID_COL + ") VALUES ( ?, ? );")) {
            for (Permission p : al.getPermissions()) {
                stmt.setInt(1, al.getAccessLevelID());
                stmt.setInt(2, p.getPermID());
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public AccessLevel getAccessLevelByID(int accessLevelId) {
        AccessLevel al = null;
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ACCESSLEVELID_COL + " = " + accessLevelId)) {

            if (rs.next()) {
                Permission_DAO pDAO = new Permission_JDBCDAO();
                al = new AccessLevel(rs.getInt(ACCESSLEVELID_COL), rs.getString(ACCESSLEVELNAME_COL), pDAO.getPermissionsByAccessLevelID(rs.getInt(ACCESSLEVELID_COL)));

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

        return resMap;
    }

    @Override
    protected AccessLevel getEntityFromResultSet(ResultSet rs
    ) {
        try {
            Permission_DAO pDAO = new Permission_JDBCDAO();
            return new AccessLevel(rs.getInt(ACCESSLEVELID_COL), rs.getString(ACCESSLEVELNAME_COL), pDAO.getPermissionsByAccessLevelID(rs.getInt(ACCESSLEVELID_COL)));
        } catch (SQLException ex) {
            Logger.getLogger(AccessLevel_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected void updateEntityWithAutoKeys(ResultSet rs, AccessLevel entity
    ) {
        try {
            entity.setAccessLevelID(rs.getInt(ACCESSLEVELID_COL));
        } catch (SQLException ex) {
            Logger.getLogger(AccessLevel_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
