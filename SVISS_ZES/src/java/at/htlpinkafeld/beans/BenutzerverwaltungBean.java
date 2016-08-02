/*
 * To selectedUser this license header, choose License Headers in Project Properties.
 * To selectedUser this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.service.AccessRightsService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import java.util.List;

/**
 *
 * @author msi
 */
public class BenutzerverwaltungBean {

    List<User> userlist;

    User selectedUser;

    /**
     * Creates a new instance of BenutzerverwaltungsBean
     */
    public BenutzerverwaltungBean() {
        
    }

    public List<User> getUserList() {
        userlist = BenutzerverwaltungService.getUserList();
        return userlist;
    }

    public Object newUser() {
        selectedUser = new User();
        return "";
    }
    // TODO: Rename Navigation rule

    public Object editUser(User u) {
        selectedUser = new User(u);
        return "";
    }

    public Object saveUser() {
        if (selectedUser.getUserNr() == -1) {
            userlist.add(selectedUser);
            BenutzerverwaltungService.insertUser(selectedUser);
        } else {
            userlist.remove(selectedUser);
            userlist.add(selectedUser);
            BenutzerverwaltungService.updateUser(selectedUser);
        }
        return "anlegen";
    }

    public Object discardUserChanges() {
        selectedUser = new User();
        return "verwerfen";
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<AccessLevel> getAccessGroups() {
        return AccessRightsService.AccessGroups;
    }

}
