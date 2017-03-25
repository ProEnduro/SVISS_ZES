/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util;

/**
 *
 * @author ÐarkHell2
 */
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

    public String getTheme() throws IOException {
        user = masterBean.getUser();
        if (user != null) {
            setTheme();
        }
        return this.theme;
    }

    public void setTheme(String theme) throws IOException {
        this.theme = theme;
        if (user != null) {
            setThemeProp(theme);
        }
    }

    public void changeTheme() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (params.containsKey("globaltheme")) {
            theme = params.get("globaltheme");
        }
    }

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
