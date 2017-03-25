/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util;

import at.htlpinkafeld.pojo.WorkTime;
import java.util.Date;
import java.util.Objects;
import org.primefaces.model.DefaultScheduleEvent;

/**
 *
 * @author msi
 */
public class WorkTimeEvent extends DefaultScheduleEvent {

    private static final long serialVersionUID = 1L;

    private WorkTime worktime;

    public WorkTimeEvent(String title, Date start, Date end, WorkTime w) {
        super(title, start, end);

        super.setStyleClass("istzeit");

        worktime = w;
    }

    public WorkTimeEvent() {
        super();
    }

    public WorkTimeEvent(String title, Date start, Date end, String style, WorkTime w) {
        super(title, start, end, style);

        worktime = w;
    }

    public WorkTime getWorktime() {
        return worktime;
    }

    public void setWorktime(WorkTime worktime) {
        this.worktime = worktime;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.worktime);
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
        final WorkTimeEvent other = (WorkTimeEvent) obj;
        if (!Objects.equals(this.worktime, other.worktime)) {
            return false;
        }
        return true;
    }

}
