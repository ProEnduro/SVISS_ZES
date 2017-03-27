/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util;

/**
 * A Pojo which is used to store the available Themes
 *
 * @author ÐarkHell2
 */
public class Theme {

    private int id;
    private String displayName;
    private String name;

    /**
     * A Constuctor for the theme
     */
    public Theme() {
    }

    /**
     * A Constuctor for the theme
     *
     * @param id unique id for the theme
     * @param displayName the name which will be shown to the user
     * @param name the internal name for the theme
     */
    public Theme(int id, String displayName, String name) {
        this.id = id;
        this.displayName = displayName;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
