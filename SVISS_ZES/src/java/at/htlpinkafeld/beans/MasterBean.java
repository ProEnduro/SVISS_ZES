/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.service.AccessRightsService;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;

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

        switch (u.getAccessLevel().getAccessLevelID()) {

            case 1:
                columns = 10;
                break;
            case 2:
                columns = 8;
                break;
            case 3:
                columns = 3;
                break;
            case 4:
                columns = 6;
                break;
        }

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

    public boolean isIstZeitenEnabled() {
        return AccessRightsService.checkPermission(user.getAccessLevel(), "INPUT_TIME");
    }

    public boolean isAbwesenheitenEnabled() {
        return AccessRightsService.checkPermission(user.getAccessLevel(), "INPUT_TIME");
    }

    public boolean isAccountVerwaltungEnabled() {
        return AccessRightsService.checkPermission(user.getAccessLevel(), "EDIT_ACCOUNT");
    }

    public boolean isBenutzerverwaltungEnabled() {
        return AccessRightsService.checkPermission(user.getAccessLevel(), "VIEW_USERS");
    }

    public boolean isAcknowledgementEnabled() {
        return AccessRightsService.checkPermission(user.getAccessLevel(), "ACKNOWLEDGE_USERS");
    }

    public boolean isAlleZeitenEnabled() {
        return AccessRightsService.checkPermission(user.getAccessLevel(), "VIEW_ALL_TIMES");
    }

    public boolean isAlleAbwesenheitenEnabled() {
        return AccessRightsService.checkPermission(user.getAccessLevel(), "VIEW_ALL_ABSENCES");
    }

    public boolean isFeiertageEintragenEnabled() {
        return AccessRightsService.checkPermission(user.getAccessLevel(), "EDIT_HOLIDAY");
    }
    
    public boolean isAuswertungEnabled(){
        return true;
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
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index.xhtml?faces-redirect=true";
    }

}
