/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util.LazyScheduleModels;

import at.htlpinkafeld.beans.util.AbsenceEvent;
import at.htlpinkafeld.beans.util.WorkTimeEvent;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.Holiday;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.HolidayService;
import at.htlpinkafeld.service.IstZeitService;
import at.htlpinkafeld.service.SollZeitenService;
import at.htlpinkafeld.service.TimeConverterService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;

/**
 *
 * @author msi
 */
public class IstZeitLazyScheduleModel extends LazyScheduleModel {

    User currentUser;

    public IstZeitLazyScheduleModel(User u) {
        currentUser = u;
    }

    @Override
    public void loadEvents(Date start, Date end) {
        super.loadEvents(start, end); //To change body of generated methods, choose Tools | Templates.

        DefaultScheduleEvent e;
        
        for (Holiday h : HolidayService.getHolidayBetweenDates(start, end)) {
            e = new DefaultScheduleEvent(h.getHolidayComment(), TimeConverterService.convertLocalDateToDate(h.getHolidayDate()), TimeConverterService.convertLocalDateToDate(h.getHolidayDate()));
            e.setAllDay(true);
            e.setStyleClass("feiertag");

            this.addEvent(e);
        }
        
        
        List<Absence> absenceList = AbsenceService.getAbsenceByUserBetweenDates(currentUser, start, end);
        

        for (Absence a : absenceList) {
            e = new AbsenceEvent(currentUser.getUsername() + " " + a.getAbsenceType().getAbsenceName(), TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

            switch (a.getAbsenceType().getAbsenceTypeID()) {
                case 1:
                    if (a.isAcknowledged() == false) {
                        e.setStyleClass("medical_leave");
                    } else {
                        e.setStyleClass("medical_leave_acknowledged");
                    }
                    break;
                case 2:
                    if (a.isAcknowledged() == false) {
                        e.setStyleClass("holiday");
                    } else {
                        e.setStyleClass("holiday_acknowledged");
                    }
                    e.setAllDay(true);
                    break;
                case 3:
                    if (a.isAcknowledged() == false) {
                        e.setStyleClass("time_compensation");
                    } else {
                        e.setStyleClass("time_compensation_acknowledged");
                    }
                    break;
                case 4:
                    if (a.isAcknowledged() == false) {
                        e.setStyleClass("business-related_absence");
                    } else {
                        e.setStyleClass("business-related_absence_acknowledged");
                    }
                    break;
            }
            this.addEvent(e);

        }

        List<WorkTime> worktimelist = IstZeitService.getWorkTimeForUserBetweenStartAndEndDate(currentUser, start, end);

        for (WorkTime w : worktimelist) {
            this.addEvent(new WorkTimeEvent(currentUser.getUsername() + " Ist-Zeit", TimeConverterService.convertLocalDateTimeToDate(w.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(w.getEndTime()), "istzeit", w));
        }

        
    }

}