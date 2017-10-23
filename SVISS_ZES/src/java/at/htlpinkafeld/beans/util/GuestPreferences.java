/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util;

import at.htlpinkafeld.beans.MasterBean;
import at.htlpinkafeld.pojo.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 * A Bean used to set the Themes for the User
 *
 * @author ÐarkHell2
 */
@ManagedBean(eager = true)
public class GuestPreferences implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient ServletContext serv = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
    private String path = serv.getRealPath("/") + "/resources/";
    private transient FacesContext context = FacesContext.getCurrentInstance();
    private transient MasterBean masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);

    private User user;
    private File file = new File(path + "themes.properties");
    private transient Enumeration enu;
    private String key;

    private String theme = "aristo";

    /**
     * Method to get the theme for the current user
     *
     * @return The theme selected for the current user
     * @throws IOException when an error occurs while reading the
     * themes.properties-File
     */
    public String getTheme() throws IOException {
        user = masterBean.getUser();
        if (user != null) {
            setTheme();
        }
        return this.theme;
    }

    /**
     * Method to set the theme for the current user
     *
     * @param theme The theme which should be set for the current user
     * @throws IOException when an error occurs while reading the
     * themes.properties-File
     */
    public void setTheme(String theme) throws IOException {
        this.theme = theme;
        if (user != null) {
            setThemeProp(theme);
        }
    }

    /**
     * Used to initialize some things
     */
    public void changeTheme() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (params.containsKey("globaltheme")) {
            theme = params.get("globaltheme");
        }
    }

    /**
     * Reads the theme for the user from the properties-file and sets it here
     *
     * @throws FileNotFoundException when an error occurs while reading the
     * themes.properties-File
     * @throws IOException when an error occurs while reading the
     * themes.properties-File
     */
    public void setTheme() throws FileNotFoundException, IOException {

        try (FileInputStream inSF = new FileInputStream(file)) {
            Properties prop = new Properties();
            prop.load(inSF);

            enu = prop.keys();

            while (enu.hasMoreElements()) {
                key = (String) enu.nextElement();

                if (key.equals(this.user.getUsername())) {
                    this.setTheme(prop.getProperty(key));
                }
            }
            inSF.close();
        }

    }

    /**
     * Method used to set the new Theme for the user
     *
     * @param newTheme the new theme to be used by the current user
     * @throws FileNotFoundException when an error occurs while reading the
     * themes.properties-File
     * @throws IOException when an error occurs while reading the
     * themes.properties-File
     */
    public void setThemeProp(String newTheme) throws FileNotFoundException, IOException {

        try (FileInputStream inSF = new FileInputStream(file)) {
            Properties prop = new Properties();
            prop.load(inSF);
            enu = prop.keys();

            while (enu.hasMoreElements()) {
                key = (String) enu.nextElement();

                if (key.equals(this.user.getUsername())) {
                    try (FileOutputStream outSF = new FileOutputStream(file)) {
                        prop.setProperty(key, newTheme);
                        prop.store(outSF, "Themes_of_user");
                        outSF.close();
                    }
                } else if (prop.containsKey(this.user.getUsername()) == false) {
                    try (FileOutputStream outSF = new FileOutputStream(file)) {
                        prop.setProperty(this.user.getUsername(), "delta");
                        prop.store(outSF, "Themes_of_user");
                        outSF.close();
                    }
                }

            }
            inSF.close();
        }
    }
}
