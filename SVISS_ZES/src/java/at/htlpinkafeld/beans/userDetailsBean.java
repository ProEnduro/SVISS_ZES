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
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
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

    double saldo;
    double überstundenNach19;
    int urlaubsanspruch;

    int selectedYear;
    List<Integer> years;

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
    
    public void reloadUsers(){
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

        saldo = 0;
        überstundenNach19 = 0;

        this.selectedDate = null;

        dates.clear();

        List<UserHistoryEntry> uhelist = UserHistoryService.getUserHistoryEntriesForUser(BenutzerverwaltungService.getUserByUsername(selectedUser));

        int firstyear = uhelist.get(0).getTimestamp().getYear();
        int lastyear = uhelist.get(uhelist.size() - 1).getTimestamp().getYear();

        List<Integer> intlist = new ArrayList<>();

        for (int i = firstyear; i <= lastyear; i++) {
            intlist.add(i);
        }

        this.setSelectedYear(firstyear);

        this.setYears(intlist);
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

//        loadMonthOverview(null);
        
        for(UserHistoryEntry uhe: UserHistoryService.getUserHistoryEntriesForUserBetweenDates(BenutzerverwaltungService.getUserByUsername(selectedUser), selectedDate, selectedDate.plusDays(1))){
            this.urlaubsanspruch = uhe.getVacation();
        }

    }

    public void loadMonthOverview(ActionEvent e) {
        this.timerowlist = new ArrayList<>();

        TimeRowDisplay trd;
        
        saldo = 0;
        überstundenNach19 = 0;

        User currentUser = BenutzerverwaltungService.getUser(selectedUser);

        int max = this.selectedDate.lengthOfMonth();

        LocalDate temp;
        temp = selectedDate.withDayOfMonth(1);

        for (int i = 0; i < max; i++) {

            trd = new TimeRowDisplay(new Holiday(temp, "nicht erschienen!"));
            trd.setReason("");

            Date start = TimeConverterService.convertLocalDateToDate(temp);
            Date end = Date.from(temp.atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant());

            List<WorkTime> worklist = IstZeitService.getWorkTimeForUserBetweenStartAndEndDate(currentUser, start, end);
            List<Absence> absencelist = AbsenceService.getAbsenceByUserBetweenDates(currentUser, start, end);
            List<Holiday> holidaylist = HolidayService.getHolidayBetweenDates(start, end);

            if (!worklist.isEmpty()) {
                trd = new TimeRowDisplay(worklist.get(0));

                saldo += Double.parseDouble(trd.getWorkTime()) - Double.parseDouble(trd.getSollZeit());
                überstundenNach19 += Double.parseDouble(trd.getOverTime19plus());

                if (worklist.size() > 1) {
                    for (WorkTime w : worklist.subList(1, worklist.size() - 1)) {
                        trd.setReason(trd.getReason() + " " + worklist.size() + ". Arbeitszeit von " + w.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " bis " + w.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " ");
                    }
                }
            }

            if (!holidaylist.isEmpty()) {
                trd.setReason(holidaylist.get(0).getHolidayComment() + " ");
            }
            if (!absencelist.isEmpty()) {
                for (Absence a : absencelist) {
                    trd.setReason(trd.getReason() + a.getAbsenceType().getAbsenceName() + " " + a.getReason() + " ");
                }
            }

            if (worklist.isEmpty() && absencelist.isEmpty() && holidaylist.isEmpty()) {
                SollZeit sollzeit = SollZeitenService.getSollZeitByUserAndDayOfWeek(currentUser, temp.getDayOfWeek());

                if (sollzeit == null) {
                    trd = new TimeRowDisplay(new Holiday(temp, ""));
                }
            }
            this.timerowlist.add(trd);

            temp = temp.plus(1, ChronoUnit.DAYS);
        }

    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getÜberstundenNach19() {
        return überstundenNach19;
    }

    public void setÜberstundenNach19(double überstundenNach19) {
        this.überstundenNach19 = überstundenNach19;
    }

    public int getUrlaubsanspruch() {
        return urlaubsanspruch;
    }

    public void setUrlaubsanspruch(int urlaubsanspruch) {
        this.urlaubsanspruch = urlaubsanspruch;
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;

        LocalDate start = LocalDate.of(selectedYear, Month.JANUARY, 1);
        LocalDate end = start.withDayOfYear(start.lengthOfYear());

        SelectItem si;

        this.selectedDate = null;

        for (UserHistoryEntry uhe : UserHistoryService.getUserHistoryEntriesForUserBetweenDates(BenutzerverwaltungService.getUserByUsername(selectedUser), start, end)) {

            si = new SelectItem(uhe.getTimestamp().toLocalDate(), uhe.getTimestamp().toLocalDate().format(DateTimeFormatter.ofPattern("MMMM")));

            if (this.selectedDate == null) {
                this.setSelectedDate((LocalDate) si.getValue());
                urlaubsanspruch = uhe.getVacation();
            }

            this.dates.add(si);
        }
    }

}
