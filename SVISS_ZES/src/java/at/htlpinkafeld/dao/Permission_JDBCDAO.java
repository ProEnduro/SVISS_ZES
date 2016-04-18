/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao;

import at.htlpinkafeld.dao.interf.Permission_DAO;
import at.htlpinkafeld.pojo.Permission;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Six
 */
class Permission_JDBCDAO extends Base_JDBCDAO<Permission> implements Permission_DAO {

    protected static final String PERMISSIONID_COL = "PERMISSIONID";
    protected static final String PERMISSIONNAME_COL = "PERMISSIONNAME";

    protected static final String TABLE_NAME = "Permission";
    protected static final String RELATION_TABLE_NAME = "AccessPerm";
    protected static final String PRIMARY_KEY = PERMISSIONID_COL;
    protected static final String[] ALL_COLUMNS = {PERMISSIONID_COL, PERMISSIONNAME_COL};

    public Permission_JDBCDAO() {
        super(TABLE_NAME, ALL_COLUMNS, PRIMARY_KEY);
    }

    @Override
    public List<Permission> getPermissionsByAccessLevelID(int id) {
        List<Permission> permissions = new LinkedList<>();
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " natural join " + AccessLevel_JDBCDAO.TABLE_NAME + " WHERE " + AccessLevel_JDBCDAO.ACCESSLEVELID_COL + " = " + id)) {

            while (rs.next()) {
                permissions.add(new Permission(rs.getInt(PERMISSIONID_COL), rs.getString(PERMISSIONNAME_COL)));

            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return permissions;
    }

    @Override
    protected Map<String, Object> entityToMap(Permission entity) {
        Map<String, Object> resMap = new HashMap<>();

        resMap.put(PERMISSIONID_COL, entity.getPermID());
        resMap.put(PERMISSIONNAME_COL, entity.getPermName());

        return resMap;
    }

    @Override
    protected Permission getEntityFromResultSet(ResultSet rs) {
        try {
            return new Permission(rs.getInt(PERMISSIONID_COL), rs.getString(PERMISSIONNAME_COL));
        } catch (SQLException ex) {
            Logger.getLogger(Permission_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected void updateEntityWithAutoKeys(ResultSet rs, Permission entity) {
        try {
            entity.setPermID(rs.getInt(PERMISSIONID_COL));
        } catch (SQLException ex) {
            Logger.getLogger(Permission_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
