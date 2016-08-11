/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.beans.util.GuestPreferences;
import at.htlpinkafeld.pojo.User;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.service.AccessRightsService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
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

    public Object login() throws IOException {
        createThemePropertie();
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
  //          setTheme();
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
    
    /*
    public void setTheme() throws FileNotFoundException, IOException{
        ServletContext serv = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String path = serv.getRealPath("/") + "/resources/";
        File file=new File(path+"themes.properties");
        Enumeration enu;
        
        FileInputStream inSF=new FileInputStream(file);
        Properties prop=new Properties();
        prop.load(inSF);
        
        String key, value;
        GuestPreferences gP=new GuestPreferences();
       
        enu=prop.keys();
        
        while(enu.hasMoreElements()){      
            key=(String)enu.nextElement();
            
            if(key.equals(this.user.getUsername())){
                gP.setTheme(prop.getProperty(key));
            }
            
            //value=prop.getProperty(key);
            
            //System.out.println(key+" "+value);
            
        }
        
    } */
    
    
    public void createThemePropertie() throws FileNotFoundException, IOException{
        ServletContext serv = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String path = serv.getRealPath("/") + "/resources/";
        File file=new File(path+"themes.properties");
        
        if(file.exists()==false){
            Properties prop=new Properties();
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
