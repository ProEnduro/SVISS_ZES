/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.interf;

import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.pojo.User;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public interface User_DAO extends Base_DAO<User> {

    public abstract User getUser(int userNr);

    public abstract User getUserByUsername(String username);

    public abstract User getUserByEmail(String email);

    public abstract List<User> getUserByDisabled(Boolean disabled);

    public abstract List<User> getUserByAccessLevel(AccessLevel accessLevel);

    public abstract List<User> getApprover(User user);

    public abstract void updateApproverOfUser(User user);

    public abstract void removeApprover(User approver);
}
