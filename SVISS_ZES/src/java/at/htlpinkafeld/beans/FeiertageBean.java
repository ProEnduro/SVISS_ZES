/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.dao.util.DAOException;
import at.htlpinkafeld.pojo.Holiday;
import at.htlpinkafeld.service.HolidayService;
import at.htlpinkafeld.service.TimeConverterService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 * Bean which is used for the "holidays.xhtml"-page
 *
 * @author Martin Six
 */
public class FeiertageBean {

    private ScheduleModel timeModel;
    private ScheduleEvent curEvent;
    private Boolean dateDisabled = false;

    /**
     * loads the Data for the Page
     */
    @PostConstruct
    public void init() {
        timeModel = new LazyScheduleModel() {
            @Override
            public void loadEvents(Date start, Date end) {
                List<Holiday> holidays = HolidayService.getHolidayBetweenDates(start, end);
                holidays.stream().map((h) -> new DefaultScheduleEvent(h.getHolidayComment(), TimeConverterService.convertLocalDateTimeToDate(h.getHolidayDate().atStartOfDay()),
                        TimeConverterService.convertLocalDateTimeToDate(h.getHolidayDate().atStartOfDay()))).map((dse) -> {
                    dse.setAllDay(true);
                    return dse;
                }).forEachOrdered((dse) -> {
                    this.addEvent(dse);
                });
            }

        };
    }

    /**
     * loads the Data for the Holidays from an .ics-File
     *
     * @param event FileUploadEvent event
     */
    public void loadEventsFromICS(FileUploadEvent event) {
        try {
            CalendarBuilder builder = new CalendarBuilder();
            net.fortuna.ical4j.model.Calendar calendar = builder.build(event.getFile().getInputstream());
            for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Check2"));

                Component component = (Component) i.next();
                String name = component.getProperty("SUMMARY").getValue();
                LocalDate date = LocalDate.parse(component.getProperty("DTSTART").getValue(), DateTimeFormatter.ofPattern("yyyyMMdd"));

                try {
                    HolidayService.insert(new Holiday(date, name));
                    //                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Check3"));
                } catch (DAOException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
                }
            }
        } catch (IOException | ParserException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Feiertage wurden geladen"));
    }

    /**
     * loads the Data for the Holidays from a special .ics-File, because Uploda
     * does not work
     *
     * @throws FileNotFoundException throw from the file
     * @throws ParserException throw from the file
     * @throws IOException throw from the file
     */
    public void load() throws FileNotFoundException, ParserException, IOException {
        ServletContext serv = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String path = serv.getRealPath("/") + "/resources/";
        File file = new File(path + "Feiertageoesterreich.ics");
        CalendarBuilder builder = new CalendarBuilder();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Check1"));
        net.fortuna.ical4j.model.Calendar calendar;
        try (FileInputStream fis = new FileInputStream(file)) {
            calendar = builder.build(fis);
        }
        for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Check2"));

            try {
                Component component = (Component) i.next();
                String name = component.getProperty("SUMMARY").getValue();
                LocalDate date = LocalDate.parse(component.getProperty("DTSTART").getValue(), DateTimeFormatter.ofPattern("yyyyMMdd"));

                HolidayService.insert(new Holiday(date, name));
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Check3"));

            } catch (DAOException ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Feiertage wurden geladen"));
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

    public Boolean getDateDisabled() {
        return dateDisabled;
    }

    public void setDateDisabled(Boolean dateDisabled) {
        this.dateDisabled = dateDisabled;
    }

    /**
     * adds a new Holiday
     */
    public void addEvent() {
        Holiday h = new Holiday(TimeConverterService.convertDateToLocalDate(curEvent.getStartDate()), curEvent.getTitle());
        HolidayService.delete(h);
        HolidayService.insert(h);
    }

    /**
     * removes a Holiday
     */
    public void removeEvent() {
        Holiday h = new Holiday(TimeConverterService.convertDateToLocalDate(curEvent.getStartDate()), curEvent.getTitle());
        HolidayService.delete(h);
    }

    public void onEventSelect(SelectEvent selectEvent) {
        curEvent = (ScheduleEvent) selectEvent.getObject();
        dateDisabled = true;
    }

    /**
     * set the curEvent according to the SelectEvent
     *
     * @param selectEvent SelectEvent
     */
    public void onDateSelect(SelectEvent selectEvent) {
        dateDisabled = false;
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

    /**
     * Moves the Holiday according to the ScheduleEntryMoveEvent
     *
     * @param e ScheduleEntryMoveEvent
     */
    public void onEventMove(ScheduleEntryMoveEvent e) {
        Holiday h = new Holiday(TimeConverterService.convertDateToLocalDate(e.getScheduleEvent().getStartDate()), e.getScheduleEvent().getTitle());
        HolidayService.insert(h);
        h = new Holiday(h.getHolidayDate().minusDays(e.getDayDelta()), h.getHolidayComment());
        HolidayService.delete(h);
    }

    public void onDialogClose(CloseEvent e) {
        dateDisabled = false;
    }

}
