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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Used for displaying different Pojos in the Datatable
 *
 * @author Martin Six
 */
@XmlRootElement
public class TimeRowDisplay {

    private static final DateTimeFormatter TIME_FORMATTER;

    static {
        TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    }

    private final LocalDate date;
    private String workTimeStart = null;
    private String workTimeEnd = null;
    private Integer breakTime = null;
    private Double sollZeit = null;
    private Double workTime = null;
    private Double overTime19plus = null;
    private String reason = null;

    public TimeRowDisplay(Holiday h) {
        date = h.getHolidayDate();
        reason = h.getHolidayComment();
    }

    public TimeRowDisplay(Absence a) {
        date = a.getStartTime().toLocalDate();
        reason = a.getAbsenceType()+ ": " + a.getReason();
        if (!a.getAbsenceType().equals(AbsenceTypeNew.HOLIDAY)) {
            reason += a.getStartTime().toLocalTime().format(TIME_FORMATTER) + " -  " + a.getEndTime().toLocalTime().format(TIME_FORMATTER);
        }
    }

    public TimeRowDisplay(WorkTime wt) {
        date = wt.getStartTime().toLocalDate();
        workTimeStart = wt.getStartTime().format(TIME_FORMATTER);
        workTimeEnd = wt.getEndTime().format(TIME_FORMATTER);
        breakTime = wt.getBreakTime();

        double temp = wt.getSollStartTime().until(wt.getSollEndTime(), ChronoUnit.MINUTES) / 60.0;
        if (temp >= 6.0) {
            sollZeit = Math.round((temp - 0.5) * 100.0) / 100.0;
        } else {
            sollZeit = Math.round(temp * 100.0) / 100.0;
        }
        workTime = Math.round((wt.getStartTime().until(wt.getEndTime(), ChronoUnit.MINUTES) / 60.0 - (breakTime / 60.0)) * 100.0) / 100.0;

        double ot19Plus = wt.getOvertimeAfter19() / 60.0;
        overTime19plus = Math.round(ot19Plus * 100.0) / 100.0;
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

    public Double getSollZeit() {
        return sollZeit;
    }

    public Double getWorkTime() {
        return workTime;
    }

    public Double getOverTime19plus() {
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
