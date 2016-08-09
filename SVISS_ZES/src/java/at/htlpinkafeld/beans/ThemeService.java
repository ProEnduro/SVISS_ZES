/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

/*
 *
 * @author ÐarkHell2
*/


import at.htlpinkafeld.beans.util.Theme;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
 
@ManagedBean(name="themeService", eager = true)

public class ThemeService {
     
    private List<Theme> themes;
     
    @PostConstruct
    public void init() {
        themes = new ArrayList<>();
        themes.add(new Theme(0, "Standard", "aristo")); 
        themes.add(new Theme(1, "Afterdark", "afterdark"));
        
        
    }
     
    public List<Theme> getThemes() {
        return themes;
    } 
}