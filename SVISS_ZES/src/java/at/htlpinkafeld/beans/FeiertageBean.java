/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.Holiday;
import at.htlpinkafeld.service.HolidayService;
import at.htlpinkafeld.service.TimeConverterService;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author Martin Six
 */
public class FeiertageBean {

    private ScheduleModel timeModel;
    private ScheduleEvent curEvent;

    @PostConstruct
    public void init() {
        timeModel = new LazyScheduleModel() {
            @Override
            public void loadEvents(Date start, Date end) {
                List<Holiday> holidays = HolidayService.getHolidayBetweenDates(start, end);
                for (Holiday h : holidays) {
                    this.addEvent(new DefaultScheduleEvent(h.getHolidayComment(), TimeConverterService.convertLocalDateTimeToDate(h.getHolidayDate().atStartOfDay()),
                            TimeConverterService.convertLocalDateTimeToDate(h.getHolidayDate().atTime(23, 59, 59))));
                }
            }

        };
    }

    public ScheduleModel getTimeModel() {
        return timeModel;
    }

    public ScheduleEvent getCurEvent() {
        return curEvent;
    }

    public void setCurEvent(ScheduleEvent curEvent) {
        this.curEvent = curEvent;
    }

    public void addEvent() {
        Holiday h = new Holiday(TimeConverterService.convertDateToLocalDate(curEvent.getStartDate()), curEvent.getTitle());
        HolidayService.delete(h);
        HolidayService.insert(h);
    }

    public void removeEvent() {
        Holiday h = new Holiday(TimeConverterService.convertDateToLocalDate(curEvent.getStartDate()), curEvent.getTitle());
        HolidayService.delete(h);
    }

    public void onEventSelect(SelectEvent selectEvent) {
        curEvent = (ScheduleEvent) selectEvent.getObject();
    }

    public void onDateSelect(SelectEvent selectEvent) {
        Date date = (Date) selectEvent.getObject();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        date = c.getTime();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        curEvent = new DefaultScheduleEvent("", date, c.getTime());
    }

    public void onEventMove(ScheduleEntryMoveEvent e) {
        Holiday h = new Holiday(TimeConverterService.convertDateToLocalDate(e.getScheduleEvent().getStartDate()), e.getScheduleEvent().getTitle());
        HolidayService.insert(h);
        h = new Holiday(h.getHolidayDate().minusDays(e.getDayDelta()), h.getHolidayComment());
        HolidayService.delete(h);
    }

}
