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

@ManagedBean(name = "themeService", eager = true)

public class ThemeService {

    private List<Theme> themes;

    @PostConstruct
    public void init() {
        themes = new ArrayList<>();
        themes.add(new Theme(0, "Afterdark", "afterdark"));
        themes.add(new Theme(1, "Delta", "delta"));
        themes.add(new Theme(2, "Trontastic", "trontastic"));
        themes.add(new Theme(3, "Glass-X", "glass-x"));
        themes.add(new Theme(4, "Blitzer", "blitzer"));
        themes.add(new Theme(5, "Le-Frog", "le-frog"));
        themes.add(new Theme(6, "Redmond", "redmond"));
        themes.add(new Theme(7, "Cruze", "cruze"));
        themes.add(new Theme(8, "Eggplant", "eggplant"));
        themes.add(new Theme(9, "Hot-Sneaks", "hot-sneaks"));
        themes.add(new Theme(10, "Dot-LUV", "dot-luv"));
    }

    public List<Theme> getThemes() {
        return themes;
    }
}
