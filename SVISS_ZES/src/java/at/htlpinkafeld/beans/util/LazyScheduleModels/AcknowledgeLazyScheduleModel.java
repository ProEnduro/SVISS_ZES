/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util.LazyScheduleModels;

import at.htlpinkafeld.beans.util.AbsenceEvent;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.TimeConverterService;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;

/**
 * A {@link LazyScheduleModel} which uses a selectedUser and loads
 * unacknowledged Absences from AbsenceService as {@link AbsenceEvent}
 * accordingly
 *
 * @author msi
 */
public class AcknowledgeLazyScheduleModel extends DefaultScheduleModel {

    private static final long serialVersionUID = 4918331375498080793L;

    private String selectedUser = null;

    /**
     * Constructor for AcknowledgeLazyScheduleModel
     */
    public AcknowledgeLazyScheduleModel() {
        loadEvents();
    }

    /**
     * Sets selected User for Event loading. setting null or "ALL" loads all
     * Absences otherwise loads Absences according to
     *
     * @param selected
     */
    public void setSelectedUser(String selected) {
        selectedUser = selected;
    }

    /**
     * Loads all of the unacknowledged Absences according to selectedUser
     *
     */
    public final void loadEvents() {

        if (selectedUser == null || selectedUser.equals("All")) {
            for (Absence a : AbsenceService.getAllUnacknowledged()) {

                AbsenceEvent e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType(), TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

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
                    default:
                }

                this.addEvent(e);
            }
        } else {
            for (Absence a : AbsenceService.getAbsenceByUserAndUnacknowledged(BenutzerverwaltungService.getUser(selectedUser))) {

                AbsenceEvent e = new AbsenceEvent(a.getReason(), TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

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
                    default:
                }

                this.addEvent(e);
            }
        }
    }

}
