/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserProxy;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;

/**
 *
 * @author ÐarkHell2
 */
public class BenutzerkontoBean {

    List<User> userlist;
    String persName;
    String userName;
    String email;
    Date hireDate;
    String password;
    double weekTime;

    FacesContext context = FacesContext.getCurrentInstance();
    MasterBean masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);
    User user;

    
    public BenutzerkontoBean() {
        user = new UserProxy();
        user = masterBean.getUser();
    }

    public List<User> getUserList() {
        return BenutzerverwaltungService.getUserList();
    }

    public AccessLevel getAccessLevel() {
        return user.getAccessLevel();
    }

    public String getPersName() {
        return user.getPersName();
    }

    public void setPersName(String persName) {
        user.setPersName(persName);
        BenutzerverwaltungService.updateUser(user);
    }

    public String getUserName() {
        return user.getUsername();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public void setEmail(String email) {
        user.setEmail(email);
        BenutzerverwaltungService.updateUser(user);
    }

    public LocalDate getHireDate() {
        return user.getHiredate();
    }

    public String getPassword() {
        return user.getPass();
    }

    public void setPassword(String password) {
        user.setPass(password); 
        BenutzerverwaltungService.updateUser(user);
    }

    public double getWeekTime() {
        return user.getWeekTime();
    }  
}