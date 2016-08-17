/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util.LazyScheduleModels;

import at.htlpinkafeld.beans.util.AbsenceEvent;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.Holiday;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.HolidayService;
import at.htlpinkafeld.service.TimeConverterService;
import java.util.Date;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;

/**
 *
 * @author msi
 */
public class AlleAbwesenheitenLazyScheduleModel extends LazyScheduleModel {

    public AlleAbwesenheitenLazyScheduleModel() {

    }

    @Override
    public void loadEvents(Date start, Date end) {
        super.loadEvents(start, end); //To change body of generated methods, choose Tools | Templates.

        DefaultScheduleEvent holidayevent;

        for (Holiday h : HolidayService.getHolidayBetweenDates(start, end)) {
            holidayevent = new DefaultScheduleEvent(h.getHolidayComment(), TimeConverterService.convertLocalDateToDate(h.getHolidayDate()), TimeConverterService.convertLocalDateToDate(h.getHolidayDate()));
            holidayevent.setAllDay(true);
            holidayevent.setStyleClass("feiertag");

            this.addEvent(holidayevent);
        }

        AbsenceEvent e;

        for (Absence a : AbsenceService.getAllAcknowledged()) {

            e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName(), TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

            switch (e.getAbsence().getAbsenceType().getAbsenceTypeID()) {

                case 1:
                    e.setStyleClass("medical_leave_acknowledged");
                    break;
                case 2:
                    e.setStyleClass("holiday_acknowledged");
                    e.setAllDay(true);
                    break;
                case 3:
                    e.setStyleClass("time_compensation_acknowledged");
                    break;
                case 4:
                    e.setStyleClass("business-related_absence_acknowledged");
                    break;

            }

            this.addEvent(e);
        }

        for (Absence a : AbsenceService.getAllUnacknowledged()) {

            e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName() + " (unacknowledged)", TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

            switch (e.getAbsence().getAbsenceType().getAbsenceTypeID()) {

                case 1:
                    e.setStyleClass("medical_leave");
                    break;
                case 2:
                    e.setStyleClass("holiday");
                    e.setAllDay(true);
                    break;
                case 3:
                    e.setStyleClass("time_compensation");
                    break;
                case 4:
                    e.setStyleClass("business-related_absence");
                    break;

            }
            this.addEvent(e);
        }

    }

}
