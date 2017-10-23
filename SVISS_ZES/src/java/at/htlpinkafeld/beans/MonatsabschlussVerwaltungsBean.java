/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserHistoryEntry;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.UserHistoryService;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 *
 * @author Martin Six
 */
public class MonatsabschlussVerwaltungsBean {

    private List<UserHistoryEntry> userHistoryEntrys;

    private boolean editing = false;

    private UserHistoryEntry selectedUserHistoryEntry;

    private List<User> users;
    private Month[] availableMonths;
    private Month selectedMonth;
    private Integer selectedYear;

    /**
     * Creates a new instance of MonatsabschlussVerwaltungsBean
     */
    public MonatsabschlussVerwaltungsBean() {
        userHistoryEntrys = UserHistoryService.getUserHistoryEntries();
        users = BenutzerverwaltungService.getUserList();
        availableMonths = Month.values();

        selectedMonth = Month.JANUARY;
        selectedYear = LocalDateTime.now().getYear();
        selectedUserHistoryEntry = new UserHistoryEntry(LocalDateTime.now(), null, 0, 0);
    }

    public List<UserHistoryEntry> getUserHistoryEntrys() {
        return userHistoryEntrys;
    }

    public boolean isEditing() {
        return editing;
    }

    public List<User> getUsers() {
        return users;
    }

    public UserHistoryEntry getSelectedUserHistoryEntry() {
        return selectedUserHistoryEntry;
    }

    public void setSelectedUserHistoryEntry(UserHistoryEntry selectedUserHistoryEntry) {
        this.selectedUserHistoryEntry = selectedUserHistoryEntry;
    }

    public Month[] getAvailableMonths() {
        return availableMonths;
    }

    public Month getSelectedMonth() {
        return selectedMonth;
    }

    public void setSelectedMonth(Month selectedMonth) {
        this.selectedMonth = selectedMonth;
    }

    public Integer getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(Integer selectedYear) {
        this.selectedYear = selectedYear;
    }

    public boolean filterByInsensitiveString(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim();
        if (filterText == null || filterText.equals("")) {
            return true;
        }

        if (value == null) {
            return false;
        }

        String carName = value.toString().toUpperCase();
        filterText = filterText.toUpperCase();

        return carName.contains(filterText);
    }

    public void editUserHistoryEntry(ActionEvent e) {
        selectedUserHistoryEntry = (UserHistoryEntry) e.getComponent().getAttributes().get("uhEntry");
        selectedMonth = selectedUserHistoryEntry.getTimestamp().getMonth();
        selectedYear = selectedUserHistoryEntry.getTimestamp().getYear();

        users = BenutzerverwaltungService.getUserList();
        editing = true;
    }

    public void saveEditedUserHistoryEntry() {
        if (selectedUserHistoryEntry.getOvertime() == null || selectedUserHistoryEntry.getVacation() == null) {
            FacesContext.getCurrentInstance().validationFailed();
        } else {
            

            if (isEditing()) {
                UserHistoryService.updateUserHistoryEntry(selectedUserHistoryEntry);
                this.userHistoryEntrys.remove(selectedUserHistoryEntry);
                this.userHistoryEntrys.add(selectedUserHistoryEntry);
            } else {
                if (this.userHistoryEntrys.contains(this.selectedUserHistoryEntry)) {
                    FacesContext.getCurrentInstance().validationFailed();
                    FacesContext.getCurrentInstance().addMessage("uhEntryDialogForm", new FacesMessage(FacesMessage.SEVERITY_WARN, "Speichern fehlgeschlagen!", "Es ist bereits ein Eintrag für diesen Benutzer in diesem Monat verfügbar!"));
                } else {
                    selectedUserHistoryEntry.setTimestamp(LocalDateTime.of(selectedYear, selectedMonth.plus(1), 1, 0, 0).minusMinutes(10));
                    UserHistoryService.insertUserHistoryEntry(selectedUserHistoryEntry);
                    this.userHistoryEntrys.add(selectedUserHistoryEntry);
                }
            }
        }
    }

    public void createUserHistoryEntry(ActionEvent e) {
        selectedUserHistoryEntry = new UserHistoryEntry(LocalDateTime.now(), null, 0, 0);
        selectedMonth = Month.JANUARY;
        selectedYear = selectedUserHistoryEntry.getTimestamp().getYear();

        users = BenutzerverwaltungService.getUserList();
        editing = false;
    }

    public String getDisplayName(Month month) {
        return month.getDisplayName(TextStyle.FULL, Locale.getDefault());
    }

    public int sortByTimestamp(Object obj1, Object obj2) {
        LocalDateTime ldt1 = (LocalDateTime) obj1;
        LocalDateTime ldt2 = (LocalDateTime) obj2;

        return ldt1.compareTo(ldt2);
    }
}
