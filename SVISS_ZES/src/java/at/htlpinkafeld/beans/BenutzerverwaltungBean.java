/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.dao.interf.DAOFactory;
import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import java.util.Date;
import java.util.List;
import javax.faces.event.ActionEvent;

/**
 *
 * @author msi
 */
public class BenutzerverwaltungBean {

    List<User> userlist;
    int al;
    String pn;
    String un;
    String email;
    Date hd;
    String pw;
    double wt;

    String newPassword;
    User change;

    /**
     * Creates a new instance of BenutzerverwaltungsBean
     */
    public BenutzerverwaltungBean() {

    }

    public List<User> getUserList() {
        return BenutzerverwaltungService.getUserList();
    }

    public Object newUserSite() {

        return "newUserSite";
    }

    public Object newUserAnlegen() {
        AccessLevel a = DAOFactory.getDAOFactory().getAccessLevelDAO().getAccessLevelByID(al);

        User u = new User(a, pn, un, email, hd, pw, wt);
        BenutzerverwaltungService.insertUser(u);

        return "anlegen";
    }

    public Object newUserVerwerfen() {

        return "verwerfen";
    }

    public int getAl() {
        return al;
    }

    public String getPn() {
        return pn;
    }

    public String getUn() {
        return un;
    }

    public String getEmail() {
        return email;
    }

    public Date getHd() {
        return hd;
    }

    public String getPw() {
        return pw;
    }

    public double getWt() {
        return wt;
    }

    public void setAl(int al) {
        this.al = al;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHd(Date hd) {
        this.hd = hd;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public void setWt(double wt) {
        this.wt = wt;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setUP(ActionEvent e) {
        change = (User) e.getComponent().getAttributes().get("val");
        System.out.println(change);
    }

    public void changePassword(ActionEvent e) {
        change.setPass(this.newPassword);
        BenutzerverwaltungService.updateUser(change);
    }

}
