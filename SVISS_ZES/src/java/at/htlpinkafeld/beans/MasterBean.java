/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.dao.util.DAODML_Observer;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.service.AccessRightsService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;

/**
 *
 * @author msi
 */
public class MasterBean implements DAODML_Observer {

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

    @PostConstruct
    public void onConstruct() {
        BenutzerverwaltungService.addUserObserver(this);
    }

    public User getUser() {
        if (user != null) {
            return BenutzerverwaltungService.getUser(user.getUserNr());
        } else {
            return user;
        }
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
                columns = 4;
                break;
            case 4:
                columns = 6;
                break;
        }

    }

    public void refreshIfNoCurrentUser() {
        if (user == null) {
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/SVISS_ZES/");
            } catch (IOException ex) {
                Logger.getLogger(MasterBean.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        if (user != null) {
            return AccessRightsService.checkPermission(user.getAccessLevel(), "INPUT_TIME");
        }
        return false;
    }

    public boolean isAbwesenheitenEnabled() {
        if (user != null) {
            return AccessRightsService.checkPermission(user.getAccessLevel(), "INPUT_TIME");
        }
        return false;
    }

    public boolean isAccountVerwaltungEnabled() {
        if (user != null) {
            return AccessRightsService.checkPermission(user.getAccessLevel(), "EDIT_ACCOUNT");
        }
        return false;
    }

    public boolean isBenutzerverwaltungEnabled() {
        if (user != null) {
            return AccessRightsService.checkPermission(user.getAccessLevel(), "VIEW_USERS");
        }
        return false;
    }

    public boolean isAcknowledgementEnabled() {
        if (user != null) {
            return AccessRightsService.checkPermission(user.getAccessLevel(), "ACKNOWLEDGE_USERS");
        }
        return false;
    }

    public boolean isAlleZeitenEnabled() {
        if (user != null) {
            return AccessRightsService.checkPermission(user.getAccessLevel(), "VIEW_ALL_TIMES");
        }
        return false;
    }

    public boolean isAlleAbwesenheitenEnabled() {
        if (user != null) {
            return AccessRightsService.checkPermission(user.getAccessLevel(), "VIEW_ALL_ABSENCES");
        }
        return false;
    }

    public boolean isFeiertageEintragenEnabled() {
        if (user != null) {
            return AccessRightsService.checkPermission(user.getAccessLevel(), "EDIT_HOLIDAY");
        }
        return false;
    }

    public boolean isAuswertungEnabled() {
        return user != null;
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
//        return null;
    }

    @PreDestroy
    private void onDestroy() {
        BenutzerverwaltungService.deleteUserObserver(this);
    }

    @Override
    public void notifyObserver() {
        if (user != null) {
            user = BenutzerverwaltungService.getUser(user.getUserNr());
        }
    }
}
