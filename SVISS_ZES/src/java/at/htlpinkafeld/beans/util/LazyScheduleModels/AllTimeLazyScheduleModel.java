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
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.HolidayService;
import at.htlpinkafeld.service.IstZeitService;
import at.htlpinkafeld.service.TimeConverterService;
import java.util.Date;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;

/**
 * A {@link LazyScheduleModel} which is used for the calendar in the All
 * Times-Page
 *
 * @author msi
 */
public class AllTimeLazyScheduleModel extends LazyScheduleModel {

//TODO  LazyLoading in Service-Layer
    private static final long serialVersionUID = 1L;

    private String selectedUser = null;

    /**
     * Constructor for AllTimeLazyScheduleModel
     */
    public AllTimeLazyScheduleModel() {

    }

    /**
     * Sets the Username of the selected User
     *
     * @param s Username of the selectedUser
     */
    public void setSelectedUser(String s) {
        selectedUser = s;
    }

    /**
     * loads Events according to the start and end Date and the selectedUser
     *
     * @param start start Date
     * @param end end Date
     */
    @Override
    public void loadEvents(Date start, Date end) {
        super.loadEvents(start, end);

        DefaultScheduleEvent holidayevent;

        for (Holiday h : HolidayService.getHolidayBetweenDates(start, end)) {
            holidayevent = new DefaultScheduleEvent(h.getHolidayComment(), TimeConverterService.convertLocalDateToDate(h.getHolidayDate()), TimeConverterService.convertLocalDateToDate(h.getHolidayDate()));
            holidayevent.setAllDay(true);
            holidayevent.setStyleClass("feiertag");

            this.addEvent(holidayevent);
        }

        if (selectedUser == null || selectedUser.equals("All")) {

            AbsenceEvent e;
            for (Absence a : AbsenceService.getAllAcknowledged()) {

                e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType(), TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

                switch (e.getAbsence().getAbsenceType()) {

                    case MEDICAL_LEAVE:
                        e.setStyleClass("medical_leave_acknowledged");
                        break;
                    case HOLIDAY:
                        e.setStyleClass("holiday_acknowledged");
                        e.setAllDay(true);
                        break;
                    case TIME_COMPENSATION:
                        e.setStyleClass("time_compensation_acknowledged");
                        break;
                    case BUSINESSRELATED_ABSENCE:
                        e.setStyleClass("business-related_absence_acknowledged");
                        break;

                }
                this.addEvent(e);
            }

            for (Absence a : AbsenceService.getAllUnacknowledged()) {

                e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType() + " (unacknowledged)", TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

                switch (e.getAbsence().getAbsenceType()) {

                    case MEDICAL_LEAVE:
                        e.setStyleClass("medical_leave");
                        break;
                    case HOLIDAY:
                        e.setStyleClass("holiday");
                        e.setAllDay(true);
                        break;
                    case TIME_COMPENSATION:
                        e.setStyleClass("time_compensation");
                        break;
                    case BUSINESSRELATED_ABSENCE:
                        e.setStyleClass("business-related_absence");
                        break;

                }
                this.addEvent(e);
            }

            IstZeitService.getAllWorkTime().stream().map((w) -> new WorkTimeEvent(w.getUser().getUsername() + " Ist-Zeit", TimeConverterService.convertLocalDateTimeToDate(w.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(w.getEndTime()), "istzeit", w)).forEachOrdered((workevent) -> {
                this.addEvent(workevent);
            });

        } else {
            for (Absence a : AbsenceService.getAbsenceByUserAndUnacknowledged(BenutzerverwaltungService.getUser(selectedUser))) {

                AbsenceEvent e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType() + " (unacknowledged)", TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

                switch (e.getAbsence().getAbsenceType()) {

                    case MEDICAL_LEAVE:
                        e.setStyleClass("medical_leave");
                        break;
                    case HOLIDAY:
                        e.setStyleClass("holiday");
                        e.setAllDay(true);
                        break;
                    case TIME_COMPENSATION:
                        e.setStyleClass("time_compensation");
                        break;
                    case BUSINESSRELATED_ABSENCE:
                        e.setStyleClass("business-related_absence");
                        break;

                }

                this.addEvent(e);
            }

            for (Absence a : AbsenceService.getAbsenceByUserAndAcknowledged(BenutzerverwaltungService.getUser(selectedUser))) {

                AbsenceEvent e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType(), TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

                switch (e.getAbsence().getAbsenceType()) {

                    case MEDICAL_LEAVE:
                        e.setStyleClass("medical_leave_acknowledged");
                        break;
                    case HOLIDAY:
                        e.setStyleClass("holiday_acknowledged");
                        e.setAllDay(true);
                        break;
                    case TIME_COMPENSATION:
                        e.setStyleClass("time_compensation_acknowledged");
                        break;
                    case BUSINESSRELATED_ABSENCE:
                        e.setStyleClass("business-related_absence_acknowledged");
                        break;

                }

                this.addEvent(e);
            }

            IstZeitService.getWorkTimeForUserBetweenStartAndEndDate(BenutzerverwaltungService.getUser(selectedUser), start, end).stream().map((w) -> new WorkTimeEvent(w.getUser().getUsername() + " Ist-Zeit", TimeConverterService.convertLocalDateTimeToDate(w.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(w.getEndTime()), "istzeit", w)).forEachOrdered((workevent) -> {
                this.addEvent(workevent);
            });
        }

    }

}
