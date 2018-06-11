/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserHistoryEntry;
import at.htlpinkafeld.pojo.WorkTime;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.AbsenceTypeNew;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.IstZeitService;
import at.htlpinkafeld.service.SollZeitenService;
import at.htlpinkafeld.service.TimeConverterService;
import at.htlpinkafeld.service.UserHistoryService;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import org.apache.commons.lang3.text.WordUtils;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Martin Six
 */
public class MonatsabschlussVerwaltungsBean {

    private List<UserHistoryEntry> userHistoryEntrys;

    private boolean editing = false;

    private UserHistoryEntry selectedUserHistoryEntry;

    private List<User> users;
    private List<SelectItem> availableMonths;
    private Month selectedMonth;
    private Integer selectedYear;

    /**
     * Creates a new instance of MonatsabschlussVerwaltungsBean
     */
    public MonatsabschlussVerwaltungsBean() {
        userHistoryEntrys = UserHistoryService.getUserHistoryEntries();
        users = BenutzerverwaltungService.getUserList();

        Month[] months = Month.values();
        availableMonths = new LinkedList<>();
        for (Month m : months) {
            availableMonths.add(new SelectItem(m, this.getDisplayName(m)));
        }

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

    public List<SelectItem> getAvailableMonths() {
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

        //System.out.println(selectedUserHistoryEntry);
        //System.out.println(selectedYear + " " + selectedMonth);
        if (selectedUserHistoryEntry.getOvertime() == null || selectedUserHistoryEntry.getVacation() == null) {
            FacesContext.getCurrentInstance().validationFailed();
        } else {

            if (isEditing()) {
                UserHistoryService.updateUserHistoryEntry(selectedUserHistoryEntry);
                this.userHistoryEntrys.remove(selectedUserHistoryEntry);
                this.userHistoryEntrys.add(selectedUserHistoryEntry);
            } else {

                selectedUserHistoryEntry.setTimestamp(LocalDateTime.of(selectedYear, selectedMonth.plus(1), 1, 0, 0).minusMinutes(10));
                //System.out.println(selectedUserHistoryEntry);

                if (this.userHistoryEntrys.contains(this.selectedUserHistoryEntry)) {
                    FacesContext.getCurrentInstance().validationFailed();
                    FacesContext.getCurrentInstance().addMessage("uhEntryDialogForm", new FacesMessage(FacesMessage.SEVERITY_WARN, "Speichern fehlgeschlagen!", "Es ist bereits ein Eintrag für diesen Benutzer in diesem Monat verfügbar!"));
                } else {
                    //selectedUserHistoryEntry.setTimestamp(LocalDateTime.of(selectedYear, selectedMonth.plus(1), 1, 0, 0).minusMinutes(10));
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

    public String getDisplayName(Month m) {
        return m.getDisplayName(TextStyle.FULL, Locale.getDefault());
    }

    public int sortByTimestamp(Object obj1, Object obj2) {
        LocalDateTime ldt1 = (LocalDateTime) obj1;
        LocalDateTime ldt2 = (LocalDateTime) obj2;

        return ldt1.compareTo(ldt2);
    }

    public void createUserHistoryEntriesForLastMonth(ActionEvent e) {
        LocalDateTime ldt = LocalDateTime.now();

        for (User u : users) {
            List<WorkTime> worklist = IstZeitService.getWorkTimeForUserBetweenStartAndEndDate(u, TimeConverterService.convertLocalDateTimeToDate(ldt.withDayOfMonth(1)), TimeConverterService.convertLocalDateTimeToDate(ldt.withDayOfMonth(ldt.toLocalDate().lengthOfMonth())));
            long uheOvertime = u.getOverTimeLeft();

            for (WorkTime t : worklist) {
                long diff = t.getStartTime().until(t.getEndTime(), ChronoUnit.MINUTES);
                diff -= t.getBreakTime();
                diff -= t.getSollStartTime().until(t.getSollEndTime(), ChronoUnit.MINUTES);
                if (t.getSollStartTime().until(t.getSollEndTime(), ChronoUnit.MINUTES) > 360) {
                    diff += 30;
                }
                uheOvertime -= diff;

                Logger.getLogger(MonatsabschlussVerwaltungsBean.class.getName()).log(Level.INFO, u.getUsername() + " " + t.getStartTime() + " " + (uheOvertime + diff) + " - " + diff);
            }

            for (Absence a : AbsenceService.getAbsencesByUserBetweenDates(u, TimeConverterService.convertLocalDateTimeToDate(ldt.withDayOfMonth(1).withHour(0).withMinute(0)), TimeConverterService.convertLocalDateTimeToDate(ldt))) {

                Logger.getLogger(MonatsabschlussVerwaltungsBean.class.getName()).log(Level.INFO, "Absence: " + ldt.getDayOfMonth() + " " + a);

                if (a.getAbsenceType().equals(AbsenceTypeNew.TIME_COMPENSATION) && ldt.getDayOfMonth() != 1 && a.isAcknowledged()) {
                    long diff = a.getStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
                    if (diff > 360) {
                        diff -= 30;
                    }
                    uheOvertime += diff;

                    Logger.getLogger(MonatsabschlussVerwaltungsBean.class.getName()).log(Level.INFO, "Absence: " + u.getUsername() + " " + a.getStartTime() + " " + (uheOvertime + diff) + " - " + diff);
                }
            }

            UserHistoryEntry uhe = new UserHistoryEntry(ldt.minusMonths(1).withDayOfMonth(ldt.minusMonths(1).toLocalDate().lengthOfMonth()).withHour(22).withMinute(59).withSecond(59), u, (int) uheOvertime, u.getVacationLeft());
            this.userHistoryEntrys.add(uhe);
            UserHistoryService.insertUserHistoryEntry(uhe);
            
            RequestContext.getCurrentInstance().update(":uhTableForm:uhTable");
        }
    }
}
