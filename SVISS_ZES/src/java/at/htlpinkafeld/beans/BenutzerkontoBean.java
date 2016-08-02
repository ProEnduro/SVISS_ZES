/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;

/**
 *
 * @author ÐarkHell2
 */
public class BenutzerkontoBean {

    List<User> userlist;
    int accessLevel;
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
        user = new User();
        user = masterBean.getUser();
    }

    public List<User> getUserList() {
        return BenutzerverwaltungService.getUserList();
    }

    public int getAccessLevel() {
        return user.getAccessLevel().getAccessLevelID();
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return user.getPass();
    }

    public void setEmail(String email) {
        user.setEmail(email);
        BenutzerverwaltungService.updateUser(user);
    }

    public Date getHireDate() {
        return user.getHiredate();
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
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

    public void setWeekTime(double weekTime) {
        this.weekTime = weekTime;
    }
    
}
