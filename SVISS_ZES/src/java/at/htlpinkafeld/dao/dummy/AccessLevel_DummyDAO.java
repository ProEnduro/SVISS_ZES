/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.AccessLevel_DAO;
import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.pojo.Permission;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author Martin Six
 */
public class AccessLevel_DummyDAO extends Base_DummyDAO<AccessLevel> implements AccessLevel_DAO {

    public AccessLevel_DummyDAO() {
        super(new LinkedList<>());
        Map<Integer, Permission> permMap = new HashMap<>();
        super.insert(new AccessLevel(3, "Reader", permMap));
        super.insert(new AccessLevel(4, "User", permMap));
        super.insert(new AccessLevel(2, "Approver", permMap));
        super.insert(new AccessLevel(1, "Admin", permMap));
    }

    @Override
    public AccessLevel getAccessLevelByID(int accessLevelId) {
        for (AccessLevel al : super.getList()) {
            if (al.getAccessLevelID() == accessLevelId) {
                return al;
            }
        }
        return null;
    }

}
