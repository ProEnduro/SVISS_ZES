/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.AccessLevel_DAO;
import at.htlpinkafeld.pojo.AccessLevel;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class AccessLevel_InMemoryDAO extends Base_InMemoryDAO<AccessLevel> implements AccessLevel_DAO {

    protected AccessLevel_InMemoryDAO() {
        super(new LinkedList<>());
        List<String> permList = new LinkedList<>();
        permList.add("VIEW_CALENDAR");
        super.insert(new AccessLevel(3, "Reader", permList));
        
        permList = new LinkedList<>();
        permList.add("INPUT_TIME");
        super.insert(new AccessLevel(4, "User", permList));
        
        permList = new LinkedList<>();
        permList.add("USER");
        permList.add("VIEW_USERS");
        permList.add("READER");
        super.insert(new AccessLevel(2, "Approver", permList));
        
        permList = new LinkedList<>();
        permList.add("APPROVER");
        permList.add("EDIT_USERS");
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

    @Override
    protected void setID(AccessLevel entity, int id) {
        entity.setAccessLevelID(id);
    }

}
