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
import at.htlpinkafeld.service.AccessRightsService;

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

        User_DAO user_dao = DAOFactory.getDAOFactory().getUserDAO();

        User u = user_dao.getUserByUsername(userString);

        this.user = u;

        if (user.isDisabled() == true) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("User is disabled!"));
        }

        if (user != null && user.getPass().equals(this.pw) && user.getUsername().contentEquals(this.userString) && user.isDisabled() == false) {

            FacesContext context = FacesContext.getCurrentInstance();
            MasterBean masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);
            masterBean.setUser(this.getUser());

            context = FacesContext.getCurrentInstance();
            ScheduleView scheduleView = (ScheduleView) context.getApplication().evaluateExpressionGet(context, "#{scheduleView}", ScheduleView.class);
            scheduleView.setUser(this.getUser());

            boolean reader = user.getAccessLevel().getAccessLevelID() == 3;

            if (reader) {
                scheduleView.loadAllTimes(null);
                return "success_reader";
            }

            this.pw = "";
            this.user = null;
            this.userString = "";

            scheduleView.reloadAbwesenheiten(null);

            return "success";
        }

        if (user == null || (user.getPass().equals(this.pw)) == false) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Invalid Password!"));
        }
        if (user == null || (user.getUsername().contentEquals(this.userString)) == false) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Invalid User!"));
        }
        return "failure";
    }

    public User getUser() {
        return this.user;
    }

}
