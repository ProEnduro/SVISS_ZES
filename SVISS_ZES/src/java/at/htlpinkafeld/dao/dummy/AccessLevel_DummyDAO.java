/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.AccessLevel_DAO;
import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.pojo.Permission;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class AccessLevel_DummyDAO extends Base_DummyDAO<AccessLevel> implements AccessLevel_DAO {

    protected AccessLevel_DummyDAO() {
        super(new LinkedList<>());
        List<Permission> permList = new ArrayList<>();
        super.insert(new AccessLevel(3, "Reader", permList));
        super.insert(new AccessLevel(4, "User", permList));
        super.insert(new AccessLevel(2, "Approver", permList));
        super.insert(new AccessLevel(1, "Admin", permList));
    }

    @Override
    public AccessLevel getAccessLevelByID(int accessLevelId) {
        for (AccessLevel al : super.getList()) {
            if (al.getAccessLevelID() == accessLevelId) {
                return clone(al);
            }
        }
        return null;
    }

    @Override
    protected AccessLevel clone(AccessLevel entity) {
        return new AccessLevel(entity);
    }

}
