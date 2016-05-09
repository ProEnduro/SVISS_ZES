/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.DAOFactory;
import at.htlpinkafeld.dao.interf.Permission_DAO;
import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.pojo.Permission;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class Permission_DummyDAO extends Base_DummyDAO<Permission> implements Permission_DAO {

    private final AccessLevel admin;
    private final AccessLevel reader;
    private final AccessLevel approver;
    private final AccessLevel user;

    protected Permission_DummyDAO() {
        super(new LinkedList<>());
        List<Permission> permList = new ArrayList<>();
        reader = new AccessLevel(3, "Reader", permList);
        user = new AccessLevel(4, "User", permList);
        approver = new AccessLevel(2, "Approver", permList);
        admin = new AccessLevel(1, "Admin", permList);
    }

    @Override
    public List<Permission> getPermissionsByAccessLevelID(int id) {
        AccessLevel al = DAOFactory.getDAOFactory().getAccessLevelDAO().getAccessLevelByID(id);
        List<Permission> permissions = new ArrayList<>();
        for (Permission p : al.getPermissions()) {
            permissions.add(clone(p));
        }
        return permissions;
    }

    @Override
    protected Permission clone(Permission entity) {
        return new Permission(entity);
    }

}
