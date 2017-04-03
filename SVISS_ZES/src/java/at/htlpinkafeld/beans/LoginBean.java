/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.service.AccessRightsService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.EmailService;
import at.htlpinkafeld.service.PasswordEncryptionService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 * The bean used for the login-page
 *
 * @author msi
 */
public class LoginBean {

    public String userString;
    public String pw;
    private User user;

    /**
     * The username or email which is entered for the password reset
     */
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

    /**
     * checks the input data and logs the user in. the User is further set in
     * the {@link MasterBean}
     *
     * @return a String for redirecting
     * @throws IOException if the Theme causes errors
     */
    public Object login() throws IOException {
        User u = null;
        try {
            createThemePropertie();
            u = BenutzerverwaltungService.getUserByUsername(userString);
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
        }
        if (u == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Invalid User!"));

            return "failure";
        }

        this.user = u;

        if (user.isDisabled() == true) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("User is disabled!"));
        }

        if (user.getPass().equals(this.pw) && user.getUsername().contentEquals(this.userString) && user.isDisabled() == false) {

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

        if (user.getPass().equals(this.pw) == false) {
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

    /**
     * sends a reset Password request to the Admins according to the inputed
     * emailOrUsername
     */
    public void sendPWResetRequest() {
        User u;
        if (emailOrUsername == null || emailOrUsername.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("passwordResetForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Es muss etwas eingegeben werden!"));
        } else {
            if (emailOrUsername.contains("@")) {
                u = BenutzerverwaltungService.getUserByEmail(emailOrUsername);
            } else {
                u = BenutzerverwaltungService.getUserByUsername(emailOrUsername);
            }
            if (u == null) {
                FacesContext.getCurrentInstance().addMessage("passwordResetForm", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Es gibt keinen passenden User!"));
            } else {
                EmailService.sendUserForgotPasswordEmail(u, BenutzerverwaltungService.getUserByAccessLevel(AccessRightsService.getAccessLevelFromName("Admin")));
                FacesContext.getCurrentInstance().addMessage("passwordResetForm", new FacesMessage("", "Anfrage für das Zurücksetzten des Passworts wurde versendet!"));
            }
        }
    }

    /**
     * creates the File for the Themes and other stuff
     *
     * @throws FileNotFoundException may be thrown
     * @throws IOException may be thrown
     */
    public void createThemePropertie() throws FileNotFoundException, IOException {
        ServletContext serv = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String path = serv.getRealPath("/") + "/resources/";
        File file = new File(path + "themes.properties");

        if (file.exists() == false) {
            Properties prop = new Properties();
            prop.setProperty("admin", "delta");

            try (FileOutputStream outSF = new FileOutputStream(file)) {
                file.createNewFile();
                prop.store(outSF, "Themes_of_user");
                outSF.close();
            }
        }

    }

}
