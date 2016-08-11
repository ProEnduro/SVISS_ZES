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
        permList.add("ALL");
        super.insert(new AccessLevel(1, "Admin", permList));

        permList = new LinkedList<>();
        permList.add("INPUT_TIME");
        permList.add("EDIT_ACCOUNT");
        permList.add("VIEW_ALL_ABSENCES");
        permList.add("ACKNOWLEDGE_USERS");
        permList.add("EDIT_HOLIDAY");
        permList.add("EVALUATE_ALL");
        super.insert(new AccessLevel(2, "Approver", permList));

        permList = new LinkedList<>();
        permList.add("VIEW_ALL_TIMES");
        permList.add("EVALUATE_ALL");
        permList.add("EDIT_ACCOUNT");
        super.insert(new AccessLevel(3, "Reader", permList));

        permList = new LinkedList<>();
        permList.add("INPUT_TIME");
        permList.add("EDIT_ACCOUNT");
        permList.add("VIEW_ALL_ABSENCES");
        permList.add("EVALUATE_SELF");
        super.insert(new AccessLevel(4, "User", permList));
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
