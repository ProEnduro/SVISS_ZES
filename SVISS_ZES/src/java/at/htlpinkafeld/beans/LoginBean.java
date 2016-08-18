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
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.EmailService;
import at.htlpinkafeld.service.PasswordEncryptionService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.ServletContext;

/**
 *
 * @author msi
 */
public class LoginBean {

    public String userString;
    public String pw;
    private User user;

    private String emailOrUsername;

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
        this.pw = PasswordEncryptionService.digestPassword(pw);
    }

    public Object login() throws IOException {
        createThemePropertie();
        User_DAO user_dao = DAOFactory.getDAOFactory().getUserDAO();

        User u = user_dao.getUserByUsername(userString);
        
        if(u == null){
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Invalid User!"));
            
            return "failure";
        }

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
                return scheduleView.loadAllTimes();
            }
            this.pw = "";
            this.user = null;
            this.userString = "";
            //          setTheme();
            scheduleView.reloadAbwesenheiten();

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

    public String getEmailOrUsername() {
        return emailOrUsername;
    }

    public void setEmailOrUsername(String emailOrUsername) {
        this.emailOrUsername = emailOrUsername;
    }

    public void sendPWResetRequest() {
        User u = null;
        if (emailOrUsername == null || emailOrUsername.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage("Es muss etwas eingegeben werden!", ""));
        } else {
            if (emailOrUsername.contains("@")) {
                u = BenutzerverwaltungService.getUserByEmail(emailOrUsername);
            } else {
                u = BenutzerverwaltungService.getUserByUsername(emailOrUsername);
            }
            if (u == null) {
                FacesContext.getCurrentInstance().addMessage("", new FacesMessage("Es gibt keinen passenden User!", ""));
            } else {
                EmailService.sendUserForgotPasswordEmail(user, BenutzerverwaltungService.getUserByAccessLevel(AccessRightsService.getAccessLevelFromName("admin")));
            }
        }
    }

    public void createThemePropertie() throws FileNotFoundException, IOException {
        ServletContext serv = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String path = serv.getRealPath("/") + "/resources/";
        File file = new File(path + "themes.properties");

        if (file.exists() == false) {
            Properties prop = new Properties();
            prop.setProperty("admin", "delta");
            prop.setProperty("user", "afterdark");

            try (FileOutputStream outSF = new FileOutputStream(file)) {
                file.createNewFile();
                prop.store(outSF, "Themes_of_user");
                outSF.close();
            }
        }

    }

}
