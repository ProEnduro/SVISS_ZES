/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Used for displaying different Pojos in the Datatable
 *
 * @author Martin Six
 */
@XmlRootElement
public class TimeRowDisplay {

    private static final DateTimeFormatter timeFormatter;

    static {
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
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

        DecimalFormat df = new DecimalFormat("#,0#");
        double temp = wt.getSollStartTime().until(wt.getSollEndTime(), ChronoUnit.MINUTES) / 60.0;
        if (temp >= 6.0) {
            sollZeit = df.format(temp - 0.5);
        } else {
            sollZeit = df.format(temp);
        }
        workTime = df.format(wt.getStartTime().until(wt.getEndTime(), ChronoUnit.MINUTES) / 60.0 - (breakTime / 60.0));

        double ot19Plus = wt.getOvertimeAfter19() / 60.0;
        overTime19plus = df.format(ot19Plus);
        reason = wt.getStartComment() + "   " + wt.getEndComment();
    }

    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    public int getDayOfMonth() {
        return date.getDayOfMonth();
    }

    public int getMonth() {
        return date.getMonthValue();
    }

    public String getDate() {
        return date.format(DateTimeFormatter.ofPattern("eee - dd.MM"));
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

    @Override
    public String toString() {
        return "TimeRowDisplay{" + "date=" + date + ", workTimeStart=" + workTimeStart + ", workTimeEnd=" + workTimeEnd + ", breakTime=" + breakTime + ", sollZeit=" + sollZeit + ", workTime=" + workTime + ", overTime19plus=" + overTime19plus + ", reason=" + reason + '}';
    }
}
