/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author msi
 */
public class MasterBean {

    User user;
    int columns;
    List<String> permissionlist;
    String page;

    /**
     * Creates a new instance of MasterBean
     */
    public MasterBean() {
//        FacesContext context = FacesContext.getCurrentInstance();
//        LoginBean loginbean = (LoginBean) context.getApplication().evaluateExpressionGet(context, "#{loginBean}", LoginBean.class);
//        setUser(loginbean.getUser());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User u) {
        permissionlist = new ArrayList<>();

        this.user = u;
        if (user.getAccessLevel().getAccessLevelID() == 1) {
            permissionlist.add("first");
            permissionlist.add("second");
        } else {
            permissionlist.add("first");
        }

        permissionlist.add("Logout");
        permissionlist.add("Abwesenheit_eintragen");
        permissionlist.add("absence_acknowledgement");
        permissionlist.add("benutzer_konto");
        permissionlist.add("alle_abwesenheiten");
        permissionlist.add("alltimes");
        columns = permissionlist.size();
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public List<String> getPermissionlist() {
        return permissionlist;
    }

    public void setPermissionlist(List<String> permissionlist) {
        this.permissionlist = permissionlist;
    }

    public boolean isFirstEnabled() {
        if (permissionlist.contains("first")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSecondEnabled() {
        if (permissionlist.contains("second")) {
            return true;
        } else {
            return false;
        }
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {

        System.out.println(page);

        switch (page) {
            case "first":
                this.page = "first.xhtml";
                break;
            case "second":
                this.page = "/WEB-INF/pages/second.xhtml";
                break;
        }

        System.out.println(this.page);
    }

    public Object logout() {
        this.user = null;
        this.page = null;

        return "failure";
    }

}
