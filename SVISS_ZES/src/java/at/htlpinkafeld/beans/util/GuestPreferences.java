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


import java.io.Serializable;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

@ManagedBean
public class GuestPreferences implements Serializable {
    
    String theme = "aristo";

	public String getTheme() {		
        		return theme;
	}

	public void setTheme(String theme) {
            this.theme = theme;
            System.out.println(this.theme);
    
	}

    
    public void changeTheme() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		if(params.containsKey("globaltheme")) {
			theme = params.get("globaltheme");
		}
    }
}