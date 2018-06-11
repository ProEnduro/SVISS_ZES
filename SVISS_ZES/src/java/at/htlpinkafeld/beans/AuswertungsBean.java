/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.service.AccessRightsService;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;

/**
 * Bean which is used to redirect to the "auswertungspages"
 *
 * @author msi
 */
public class AuswertungsBean {

    private User currentUser;

    /**
     * Creates a new instance of AuswertungsBean
     */
    public AuswertungsBean() {

    }

    /**
     * reloads the current User
     */
    public void reloadCurrentUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        MasterBean masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);
        currentUser = masterBean.getUser();
    }

    /**
     * does something
     *
     * @return new URL
     */
    public String loadAuswertungsBean() {
        reloadCurrentUser();

        return "/pages/auswertungspages/jahresuebersicht.xhtml?faces-redirect=true";
    }

    public String redirectToOverview() {
        return "/pages/auswertungspages/overview.xhtml?faces-redirect=true";
    }

    public String redirectToAlleAbwesenheiten() {
        return "/pages/auswertungspages/alleAbwesenheiten.xhtml?faces-redirect=true";
    }

    public String redirectToJahresuebersicht() {
        return "/pages/auswertungspages/jahresuebersicht.xhtml?faces-redirect=true";
    }

    public String redirectToUserDetails() {
        return "/pages/auswertungspages/userDetails.xhtml?faces-redirect=true";
    }

    public boolean getCanCurrentUserAccessMonatsabschlussVerwaltung() {
        try {
            return AccessRightsService.checkPermission(currentUser.getAccessLevel(), "EDIT_MONTHLY_RECORDS");
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/SVISS_ZES/");
            } catch (IOException ex1) {
                Logger.getLogger(AuswertungsBean.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        return false;
    }

    public boolean getCanCurrentUserAccessUeberstundenUebersicht() {
        try {
            return AccessRightsService.checkPermission(currentUser.getAccessLevel(), "EVALUATE_ALL");
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/SVISS_ZES/");
            } catch (IOException ex1) {
                Logger.getLogger(AuswertungsBean.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        return false;
    }

    public String redirectToMonatsabschlussVerwaltung() {
        return "/pages/auswertungspages/monatsabschlussVerwaltung.xhtml?faces-redirect=true";
    }

    public String redirectToUeberstundenUebersicht() {
        return "/pages/auswertungspages/ueberstundenUebersicht.xhtml?faces-redirect=true";
    }

}
