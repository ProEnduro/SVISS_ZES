/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.dao.ConnectionManager;
import at.htlpinkafeld.dao.User_DAO;
import at.htlpinkafeld.dao.User_JDBCDAO;

/**
 *
 * @author msi
 */
public class LoginBean {

    public String user;
    public String pw;
    /**
     * Creates a new instance of LoginBean
     */
    public LoginBean() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }
    
    public Object login(){
        
        System.out.println(user);
        System.out.println(pw);
        
        User_DAO user_dao = ConnectionManager.getUserDAO();
//        
//        
        System.out.println(user_dao.getUserByUsername(user));
//        
//        if((user_dao.getUserByUsername(user)) == null)
//        return "failure";
        
        return "success";
    }
    
}
