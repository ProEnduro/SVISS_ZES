/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.Permission_DAO;
import at.htlpinkafeld.pojo.Permission;
import at.htlpinkafeld.pojo.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class Permission_JDBCDAO implements Permission_DAO {

    private static final String TABLE_NAME = "Permission";
    private static final String PERMISSIONID_COL = "PERMISSIONID";
    private static final String PERMISSIONNAME_COL = "PERMISSIONNAME";

    private final static String GET_ALLPERMISSION_STATEMENT = "SELECT * FROM " + TABLE_NAME;

    @Override
    public List<Permission> getList() {
        List<Permission> permList = new ArrayList<>();
        
        
        return permList;
    }

    @Override
    public void insert(Permission o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(Permission o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Permission o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
