/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util;

import at.htlpinkafeld.pojo.WorkTime;
import java.util.Date;
import org.primefaces.model.DefaultScheduleEvent;

/**
 *
 * @author msi
 */
public class WorkTimeEvent extends DefaultScheduleEvent {

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

}
