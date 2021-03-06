/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util;

import at.htlpinkafeld.pojo.Absence;
import java.util.Date;
import java.util.Objects;
import org.primefaces.model.DefaultScheduleEvent;

/**
 * A {@link DefaultScheduleEvent} which wrapps a {@link Absence}
 *
 * @author msi
 */
public class AbsenceEvent extends DefaultScheduleEvent {

    private static final long serialVersionUID = 1L;

    private Absence absence;

    /**
     * Constructor for an AbsenceEvent
     *
     * @param title title of the event
     * @param start start-Date of the Event
     * @param end end-Date of the Event
     * @param a the wrapped Absence
     */
    public AbsenceEvent(String title, Date start, Date end, Absence a) {
        super(title, start, end);

        absence = a;
    }

    /**
     * Constructor for an AbsenceEvent
     */
    public AbsenceEvent() {
        super();
    }

    /**
     * gets the wrapped Absence
     *
     * @return the wrapped {@link Absence}
     */
    public Absence getAbsence() {
        return absence;
    }

    /**
     * sets the wrapped Absence
     *
     * @param a the new Absence
     */
    public void setAbsence(Absence a) {
        absence = a;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.absence);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbsenceEvent other = (AbsenceEvent) obj;
        return Objects.equals(this.absence, other.absence);
    }

}
