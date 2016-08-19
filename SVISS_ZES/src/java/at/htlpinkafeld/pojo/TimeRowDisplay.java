 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Used for displaying different Pojos in the Datatable
 *
 * @author Martin Six
 */
public class TimeRowDisplay {

    private static final DateTimeFormatter timeFormatter;

    static {
        timeFormatter = DateTimeFormatter.ofPattern("HH:MM");
    }
    
    private final LocalDate date;
    private String workTimeStart = null;
    private String workTimeEnd = null;
    private Integer breakTime = null;
    private String sollZeit = null;
    private String workTime = null;
    private String overTime19plus = null;
    private String reason = null;

    public TimeRowDisplay(Holiday h) {
        date = h.getHolidayDate();
        reason = h.getHolidayComment();
    }

    public TimeRowDisplay(Absence a) {
        date = a.getStartTime().toLocalDate();
        reason = a.getAbsenceType().getAbsenceName() + ": " + a.getReason();
        if (!a.getAbsenceType().getAbsenceName().contentEquals("holiday")) {
            reason += a.getStartTime().toLocalTime().format(timeFormatter) + " -  " + a.getEndTime().toLocalTime().format(timeFormatter);
        }
    }

    public TimeRowDisplay(WorkTime wt) {
        date = wt.getStartTime().toLocalDate();
        workTimeStart = wt.getStartTime().format(timeFormatter);
        workTimeEnd = wt.getEndTime().format(timeFormatter);
        breakTime = wt.getBreakTime();
        sollZeit = String.valueOf(wt.getSollStartTime().until(wt.getSollEndTime(), ChronoUnit.MINUTES) / 60.0);
        workTime = String.valueOf(wt.getStartTime().until(wt.getEndTime(), ChronoUnit.MINUTES) / 60.0);
        overTime19plus = String.valueOf(wt.getOvertimeAfter19());
    }

    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    public int getDayOfMonth() {
        return date.getDayOfMonth();
    }

    public String getWorkTimeStart() {
        return workTimeStart;
    }

    public String getWorkTimeEnd() {
        return workTimeEnd;
    }

    public Integer getBreakTime() {
        return breakTime;
    }

    public String getSollZeit() {
        return sollZeit;
    }

    public String getWorkTime() {
        return workTime;
    }

    public String getOverTime19plus() {
        return overTime19plus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
