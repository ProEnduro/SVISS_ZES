/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.Permission_DAO;
import at.htlpinkafeld.pojo.Permission;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class Permission_DummyDAO extends Base_DummyDAO<Permission> implements Permission_DAO {

    public Permission_DummyDAO() {
        super(new LinkedList<>());

    }

    @Override
    public List<Permission> getPermissionsByAccessLevelID(int id) {
        List<Permission> permissions;

        return null;
    }

}
