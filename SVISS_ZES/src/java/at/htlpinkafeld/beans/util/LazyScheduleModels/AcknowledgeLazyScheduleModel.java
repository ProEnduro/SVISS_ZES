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
import java.util.Date;
import org.primefaces.model.LazyScheduleModel;

/**
 *
 * @author msi
 */
public class AcknowledgeLazyScheduleModel extends LazyScheduleModel {

    String selectedUser = null;

    public AcknowledgeLazyScheduleModel() {

    }

    public void setSelectedUser(String selected) {
        selectedUser = selected;
    }

    @Override
    public void loadEvents(Date start, Date end) {
        super.loadEvents(start, end); //To change body of generated methods, choose Tools | Templates.

        if (selectedUser == null || selectedUser.equals("All")) {
            for (Absence a : AbsenceService.getAllUnacknowledged()) {

                AbsenceEvent e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName(), TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

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
        } else {
            for (Absence a : AbsenceService.getAbsenceByUserAndUnacknowledged(BenutzerverwaltungService.getUser(selectedUser))) {

                AbsenceEvent e = new AbsenceEvent(a.getReason(), TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

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

}
