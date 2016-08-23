/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util;

import at.htlpinkafeld.pojo.Absence;
import java.util.Date;
import org.primefaces.model.DefaultScheduleEvent;

/**
 *
 * @author msi
 */
public class AbsenceEvent extends DefaultScheduleEvent {

    private Absence absence;

    public AbsenceEvent(String title, Date start, Date end, Absence a) {
        super(title, start, end);

        absence = a;
    }

    public AbsenceEvent() {
        super();
    }

    public Absence getAbsence() {
        return absence;
    }

    public void setAbsence(Absence a) {
        absence = a;
    }
}
