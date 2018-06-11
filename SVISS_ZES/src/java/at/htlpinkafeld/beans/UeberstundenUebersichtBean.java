/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.UserHistoryEntry;
import at.htlpinkafeld.pojo.UserOvertimeRow;
import at.htlpinkafeld.service.UserHistoryService;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

/**
 *
 * @author SVISS_NES
 */
public class UeberstundenUebersichtBean {

    /**
     * Creates a new instance of UeberstundenUebersichtBean
     */
    int selectedYear;
    List<Integer> years;
    List<SelectItem> availableMonthLocalDates;
    LocalDate selectedDate;

    double ueberstundengesamt = 0;

    List<UserOvertimeRow> rows;

    public UeberstundenUebersichtBean() {
        availableMonthLocalDates = new ArrayList<>();

        List<UserHistoryEntry> uhelist = UserHistoryService.getUserHistoryEntries();

        if (!uhelist.isEmpty()) {
            int firstyear = uhelist.get(0).getTimestamp().getYear();
            int lastyear = uhelist.get(uhelist.size() - 1).getTimestamp().getYear();

            List<Integer> intlist = new ArrayList<>();

            for (int i = firstyear; i <= lastyear; i++) {
                intlist.add(i);
            }

            this.setSelectedYear(firstyear);
            this.setYears(intlist);
        }
        rows = new ArrayList<>();
    }

    public void loadMonths() {
        this.availableMonthLocalDates.clear();

        LocalDate start = LocalDate.of(selectedYear, Month.JANUARY, 1);
        LocalDate end = start.plusYears(1);

        SelectItem si;
        SelectItem oldSi = null;

        this.selectedDate = null;

        for (UserHistoryEntry uhe : UserHistoryService.getUserHistoryEntriesBetweenDates(start, end)) {

            si = new SelectItem(uhe.getTimestamp().toLocalDate(), uhe.getTimestamp().toLocalDate().format(DateTimeFormatter.ofPattern("MMMM")));

            if (oldSi != null && oldSi.getLabel().equals(si.getLabel())) {
                this.availableMonthLocalDates.remove(oldSi);
            }

            if (this.selectedDate == null) {
                this.setSelectedDate((LocalDate) si.getValue());
            }

            this.availableMonthLocalDates.add(si);
            oldSi = si;
        }
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
        loadMonths();
    }

    public List<SelectItem> getAvailableMonthLocalDates() {
        return availableMonthLocalDates;
    }

    public void setAvailableMonthLocalDates(List<SelectItem> availableMonthLocalDates) {
        this.availableMonthLocalDates = availableMonthLocalDates;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    public double getUeberstundengesamt() {
        // Konvertierung von Minuten in Stunden
        return ueberstundengesamt / 60.0;
    }

    public void setUeberstundengesamt(double ueberstundengesamt) {
        this.ueberstundengesamt = ueberstundengesamt;
    }

    public List<UserOvertimeRow> getRows() {
        return rows;
    }

    public void setRows(List<UserOvertimeRow> rows) {
        this.rows = rows;
    }

    UserHistoryEntry uheold = null;

    public void loadData(ActionEvent e) {

        Logger.getLogger(UeberstundenUebersichtBean.class.getName()).log(Level.INFO, selectedDate.toString());

        List<UserHistoryEntry> uheoldlist = UserHistoryService.getUserHistoryEntriesBetweenDates(selectedDate.minusMonths(1), selectedDate.minusDays(1));
        List<UserHistoryEntry> uhelist = UserHistoryService.getUserHistoryEntriesBetweenDates(selectedDate, selectedDate.plusMonths(1));

        this.rows = new ArrayList<>();

        //Logger.getLogger(UeberstundenUebersichtBean.class.getName()).log(Level.INFO, "uheoldlist: " + uheoldlist.toString());
        //Logger.getLogger(UeberstundenUebersichtBean.class.getName()).log(Level.INFO, "uhelist: " + uhelist.toString());
        for (UserHistoryEntry uhe : uhelist) {
            uheold = null;
            uheoldlist.forEach((old) -> {
                if (old.getUser().getUserNr().equals(uhe.getUser().getUserNr())) {
                    uheold = old;
                }
            });

            UserOvertimeRow uor = new UserOvertimeRow("null-Row", 0, 0, 0);

            if (uheold != null) {

                Logger.getLogger(UeberstundenUebersichtBean.class.getName()).log(Level.INFO, "uheold: " + uheold.toString());

                double mehrstunden = uhe.getOvertime() - uheold.getOvertime();
                double übertrag = uhe.getOvertime() - mehrstunden;
                double gesamt = mehrstunden + übertrag;

                ueberstundengesamt = ueberstundengesamt + gesamt;

                uor = new UserOvertimeRow(uhe.getUser().getPersName(), mehrstunden / 60.0, übertrag / 60.0, gesamt / 60.0);
            } else {
                double mehrstunden = uhe.getOvertime();

                ueberstundengesamt = ueberstundengesamt + mehrstunden;

                uor = new UserOvertimeRow(uhe.getUser().getPersName(), mehrstunden / 60.0, 0, mehrstunden / 60.0);
            }

            //Logger.getLogger(UeberstundenUebersichtBean.class.getName()).log(Level.INFO, uor.toString());
            this.rows.add(uor);
        }
    }

}
