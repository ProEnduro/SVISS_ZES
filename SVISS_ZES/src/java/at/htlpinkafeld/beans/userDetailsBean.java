/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.Holiday;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.TimeRowDisplay;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserHistoryEntry;
import at.htlpinkafeld.pojo.WorkTime;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.HolidayService;
import at.htlpinkafeld.service.IstZeitService;
import at.htlpinkafeld.service.SollZeitenService;
import at.htlpinkafeld.service.TimeConverterService;
import at.htlpinkafeld.service.UserHistoryService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

/**
 *
 * @author msi
 */
public class userDetailsBean {

    /**
     * Creates a new instance of userDetailsBean
     */
    List<String> userAsStringList;
    String selectedUser;
    List<SelectItem> dates;
    LocalDate selectedDate;

    List<TimeRowDisplay> timerowlist;

    public List<TimeRowDisplay> getTimerowlist() {
        return timerowlist;
    }

    public void setTimerowlist(List<TimeRowDisplay> timerowlist) {
        this.timerowlist = timerowlist;
    }

    public userDetailsBean() {
        userAsStringList = new ArrayList<>();

        for (User u : BenutzerverwaltungService.getUserList()) {
            userAsStringList.add(u.getUsername());
        }

        dates = new ArrayList<>();

    }

    public List<String> getUserAsStringList() {
        return userAsStringList;
    }

    public void setUserAsStringList(List<String> userAsStringList) {
        this.userAsStringList = userAsStringList;
    }

    public String getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(String selectedUser) {
        this.selectedUser = selectedUser;

        SelectItem si;

//        dates.clear();

        for (UserHistoryEntry uhe : UserHistoryService.getUserHistoryEntriesForUser(BenutzerverwaltungService.getUserByUsername(selectedUser))) {
            si = new SelectItem(uhe.getTimestamp().toLocalDate(), uhe.getTimestamp().toLocalDate().format(DateTimeFormatter.ofPattern("MMMM yyyy")));

            if (this.selectedDate == null) {
                this.setSelectedDate((LocalDate) si.getValue());
            }

            this.dates.add(si);
        }

    }

    public List<SelectItem> getDates() {
        return dates;
    }

    public void setDates(List<SelectItem> dates) {
        this.dates = dates;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
        
        loadMonthOverview(null);
    }

    public void loadMonthOverview(ActionEvent e) {

        System.out.println("command 1");
        
        this.timerowlist = new ArrayList<>();

        TimeRowDisplay trd;

        User currentUser = BenutzerverwaltungService.getUser(selectedUser);

        int max = this.selectedDate.lengthOfMonth();

        LocalDate temp;

        for (int i = 0; i < max; i++) {
            temp = selectedDate.plus(i, ChronoUnit.DAYS);

            trd = new TimeRowDisplay(new Holiday(temp, "nicht erschienen!"));

            Date start = TimeConverterService.convertLocalDateToDate(temp);
            Date end = Date.from(temp.atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant());

            List<WorkTime> worklist = IstZeitService.getWorkTimeForUserBetweenStartAndEndDate(currentUser, start, end);
            List<Absence> absencelist = AbsenceService.getAbsenceByUserBetweenDates(currentUser, start, end);
            List<Holiday> holidaylist = HolidayService.getHolidayBetweenDates(start, end);

            if (worklist.isEmpty() && absencelist.isEmpty() && holidaylist.isEmpty()) {
                SollZeit sollzeit = SollZeitenService.getSollZeitByUserAndDayOfWeek(currentUser, temp.getDayOfWeek());

                if (sollzeit == null) {
                    trd = new TimeRowDisplay(new Holiday(temp, "wird nicht gearbeitet"));
                }
            }

            System.out.println(trd.getDayOfMonth() + " " + trd.getReason());
            this.timerowlist.add(trd);
        }

    }

}
