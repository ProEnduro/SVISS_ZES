/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.User;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.User_DAO;

/**
 *
 * @author msi
 */
public class LoginBean {

    public String userString;
    public String pw;
    private User user;

    /**
     * Creates a new instance of LoginBean
     */
    public LoginBean() {
    }

    public String getUserString() {
        return userString;
    }

    public void setUserString(String user) {
        this.userString = user;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public Object login() {

        System.out.println(userString);
        System.out.println(pw);

        User_DAO user_dao = DAOFactory.getDAOFactory().getUserDAO();

        User u = user_dao.getUserByUsername(userString);

        this.user = u;

        System.out.println(u);

        if(user.isDisabled() == true){
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("User is disabled!"));
        }
        
        if (user != null && user.getPass().equals(this.pw) && user.isDisabled() == false) {

            FacesContext context = FacesContext.getCurrentInstance();
            MasterBean masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);
            masterBean.setUser(this.getUser());

            this.pw = "";
            this.user = null;
            this.userString = "";

            System.out.println("success!");

            return "success";
        }
        

        if (user == null || (user.getPass().equals(this.pw)) == false) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Invalid Password!"));
        }

        return "failure";
    }

    public User getUser() {
        return this.user;
    }

}
