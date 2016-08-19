/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import java.util.ArrayList;
import java.util.List;



/**
 *
 * @author msi
 */
public class alleAbwesenheitenBean {
    List<Absence> absence;
 
    User currentUser;
    
    private String selectedUser;
    private List<String> allUsers;

    public alleAbwesenheitenBean() {
        absence=AbsenceService.getAllAbsences();
        
        this.loadUserSelect();
        
    }

    public List<Absence> getAbsence() {
        return absence;
    }

    public void setAbsence(List<Absence> absence) {
        this.absence = absence;
    }

    public String getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(String selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<String> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(List<String> allUsers) {
        this.allUsers = allUsers;
    }

    
    public String loadUserSelect() {
        this.selectedUser = "All";
        this.allUsers = new ArrayList<>();

        allUsers.add("All");

        for (User u : BenutzerverwaltungService.getUserList()) {
            allUsers.add(u.getUsername());
        }
        
        return "/pages/alleAbwesenheiten.xhtml?faces-redirect=true";
    }

    public void loadAllAbsenceByUser (){
        currentUser=BenutzerverwaltungService.getUserByUsername(selectedUser);
        
        if(selectedUser.equals("All")){
            absence=AbsenceService.getAllAbsences();
        }else{
            absence=AbsenceService.getAbsenceByUser(currentUser);
        }
    }
}