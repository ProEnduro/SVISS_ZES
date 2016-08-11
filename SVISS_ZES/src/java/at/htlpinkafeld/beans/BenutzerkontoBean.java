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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

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
    
    String themeTest="1";

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

    
    public void readPropertie() throws FileNotFoundException, IOException{
        ServletContext serv = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String path = serv.getRealPath("/") + "/resources/";
        File file=new File(path+"themes.properties");
        
        String key, value;
        
        FileInputStream inSF=new FileInputStream(file);
        Properties prop=new Properties();
        prop.load(inSF);
        
        Enumeration enu=prop.keys();
        
        while(enu.hasMoreElements()){      
            key=(String)enu.nextElement();
            value=prop.getProperty(key);
            
            System.out.println(key+" "+value);
            
        }
    }
}