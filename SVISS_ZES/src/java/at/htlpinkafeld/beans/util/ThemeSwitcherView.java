/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util;

import at.htlpinkafeld.beans.ThemeService;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

/**
 * The Bean which is used to manage the themes
 *
 * @author ÐarkHell2
 */
@ManagedBean
public class ThemeSwitcherView {

    private List<Theme> themes;

    @ManagedProperty("#{themeService}")
    private ThemeService service;

    /**
     * initializes the available themes
     */
    @PostConstruct
    public void init() {
        if (service != null) {
            themes = service.getThemes();
        }
    }

    /**
     * Gets a List of all the available Themes
     *
     * @return List of Themes
     */
    public List<Theme> getThemes() {
        return themes;
    }

    /**
     * Sets the ThemeService
     *
     * @param service a ThemeService
     */
    public void setService(ThemeService service) {
        this.service = service;
    }
}
