/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.beans.util.AbsenceEvent;
import at.htlpinkafeld.beans.util.WorkTimeEvent;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.AbsenceType;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.IstZeitService;
import at.htlpinkafeld.service.SollZeitenService;
import at.htlpinkafeld.service.TimeConverterService;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

public class ScheduleView implements Serializable {

    AbsenceType type;
    List<AbsenceType> types;
    User currentUser;

    private ScheduleModel eventModel;

    private ScheduleModel acknowledgementModel;

    private ScheduleEvent event = new DefaultScheduleEvent();

    private AbsenceEvent absenceEvent = new AbsenceEvent();
    private WorkTimeEvent worktimeEvent = new WorkTimeEvent();

    private String selectedUser;
    private List<String> allUsers;

    double verbleibend;
    double overtimeleft;

    String startcomment;
    String endcomment;
    String reason;

    int breaktime;

    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();

        acknowledgementModel = new DefaultScheduleModel();

        types = AbsenceService.getList();
        type = types.get(0);

        FacesContext context = FacesContext.getCurrentInstance();
        MasterBean masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);
        currentUser = masterBean.getUser();

        this.reloadAbwesenheiten(null);
    }

    public int getBreaktime() {

        if (event.getStartDate() != null && event.getEndDate() != null) {
            if (IstZeitService.breakTimeBooleanCalc(event.getStartDate(), event.getEndDate())) {
                return 30;
            } else {
                return 0;
            }
        }

        return breaktime;
    }

    public void setBreaktime(int breaktime) {
        this.breaktime = breaktime;
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public ScheduleModel getAcknowlegementModel() {
        return acknowledgementModel;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public void addEvent(ActionEvent actionEvent) {
        if (event.getId() == null) {
            DefaultScheduleEvent e = (DefaultScheduleEvent) event;

            Absence a = new Absence(this.currentUser, type, TimeConverterService.convertDateToLocalDateTime(e.getStartDate()), TimeConverterService.convertDateToLocalDateTime(e.getEndDate()));
            a.setReason(this.reason);

            AbsenceEvent ev = new AbsenceEvent(currentUser.getUsername() + " " + type.getAbsenceName() + (a.isAcknowledged() ? "" : " (unacknowledged)"), event.getStartDate(), event.getEndDate(), a);

            switch (this.type.getAbsenceTypeID()) {
                case 1:
                    ev.setStyleClass("medical_leave");
                    break;
                case 2:
                    ev.setStyleClass("holiday");

                    LocalDateTime time = TimeConverterService.convertDateToLocalDateTime(ev.getEndDate());
                    Date d = TimeConverterService.convertLocalDateTimeToDate(time.plusMinutes(1439));
                    ev.setEndDate(d);

                    ev.setAllDay(true);
                    break;
                case 3:
                    ev.setStyleClass("time_compensation");
                    break;
                case 4:
                    ev.setStyleClass("business-related_absence");
                    break;
            }
            AbsenceService.insertAbsence(a);

            eventModel.addEvent(ev);
        } else {
            if (event instanceof WorkTimeEvent) {
                IstZeitService.update(((WorkTimeEvent) event).getWorktime());
            } else if (event instanceof AbsenceEvent) {
                AbsenceService.updateAbsence(((AbsenceEvent) event).getAbsence());
            }
            eventModel.updateEvent(event);
        }

        event = new DefaultScheduleEvent();
    }

    public void deleteEvent(ActionEvent actionEvent) {

        if (event.getId() != null) {
            if (event instanceof AbsenceEvent) {
                AbsenceEvent absenceEvent = (AbsenceEvent) event;
                if (absenceEvent.getAbsence().isAcknowledged() == false) {
                    AbsenceService.removeAbsence(absenceEvent.getAbsence());
                    eventModel.deleteEvent(event);
                    event = new DefaultScheduleEvent();
                } else {
                    FacesContext context = FacesContext.getCurrentInstance();

                    context.addMessage(null, new FacesMessage("Failed", "You can't delete acknowledged absences!"));
                }
            }
        }

    }

    public void addIstZeitEvent(ActionEvent actionEvent) {
        if (event.getId() == null) {
            WorkTimeEvent e = new WorkTimeEvent(this.currentUser.getUsername() + " " + "Ist-Zeit", event.getStartDate(), event.getEndDate(), new WorkTime(currentUser, TimeConverterService.convertDateToLocalDateTime(this.event.getStartDate()), TimeConverterService.convertDateToLocalDateTime(this.event.getEndDate()), 0, "", ""));

            e.getWorktime().setStartComment(startcomment);
            e.getWorktime().setEndComment(endcomment);
            e.getWorktime().setBreakTime(breaktime);

            e.setStyleClass("istzeit");
            IstZeitService.addIstTime(e.getWorktime());

            eventModel.addEvent(e);

        } else {

            if (event instanceof WorkTimeEvent) {
                WorkTime time = ((WorkTimeEvent) event).getWorktime();

                time.setStartTime(TimeConverterService.convertDateToLocalDateTime(event.getStartDate()));
                time.setEndTime(TimeConverterService.convertDateToLocalDateTime(event.getEndDate()));

                IstZeitService.update(time);
            } else if (event instanceof AbsenceEvent) {

                AbsenceService.updateAbsence(((AbsenceEvent) event).getAbsence());
            }

            eventModel.updateEvent(event);
        }

        event = new DefaultScheduleEvent();
    }

    public void deleteIstZeitEvent(ActionEvent e) {
        if (event.getId() != null) {
            if (event instanceof WorkTimeEvent) {
                eventModel.deleteEvent(event);
                WorkTimeEvent workevent = (WorkTimeEvent) event;
                IstZeitService.delete(workevent.getWorktime());
                event = new DefaultScheduleEvent();
            }
        }
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();

        startcomment = "";
        endcomment = "";
        breaktime = 0;
        reason = "";

        if (event instanceof WorkTimeEvent) {
            event = (WorkTimeEvent) event;
            WorkTimeEvent ev = (WorkTimeEvent) event;
            startcomment = ev.getWorktime().getStartComment();
            endcomment = ev.getWorktime().getEndComment();
            breaktime = ev.getWorktime().getBreakTime();

            RequestContext.getCurrentInstance().execute("PF('eventDialog').show();");

        } else if (event instanceof AbsenceEvent) {
            event = (AbsenceEvent) event;
            AbsenceEvent ev = (AbsenceEvent) event;
            reason = ev.getAbsence().getReason();
            RequestContext.getCurrentInstance().execute("PF('absenceDialog').show();");
        }
    }

    public void onEventInAbwesenheitenSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();

        startcomment = "";
        endcomment = "";
        breaktime = 0;
        reason = "";

        if (event instanceof WorkTimeEvent) {
            event = (WorkTimeEvent) event;
            WorkTimeEvent ev = (WorkTimeEvent) event;
            startcomment = ev.getWorktime().getStartComment();
            endcomment = ev.getWorktime().getEndComment();
            breaktime = ev.getWorktime().getBreakTime();

            RequestContext.getCurrentInstance().execute("PF('worktimeDialog').show();");

        } else if (event instanceof AbsenceEvent) {
            event = (AbsenceEvent) event;
            AbsenceEvent ev = (AbsenceEvent) event;
            reason = ev.getAbsence().getReason();
            RequestContext.getCurrentInstance().execute("PF('absenceDialog').show();");
        }
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }

    public AbsenceType getType() {
        return type;
    }

    public void setType(AbsenceType type) {
        this.type = type;
    }

    public List<AbsenceType> getTypes() {
        return types;
    }

    public void setAcknowledged(ActionEvent actionEvent) {

        if (absenceEvent.getId() == null) {

        } else {

            acknowledgementModel.deleteEvent(absenceEvent);

            absenceEvent.getAbsence().setAcknowledged(true);
            AbsenceService.updateAbsence(absenceEvent.getAbsence());

            if (absenceEvent.getAbsence().getAbsenceType().getAbsenceTypeID() == 2) {
                LocalDateTime las = absenceEvent.getAbsence().getStartTime();
                LocalDateTime lae = absenceEvent.getAbsence().getEndTime();

                int days = lae.getDayOfYear() - las.getDayOfYear() + 1;

                User u = absenceEvent.getAbsence().getUser();
                u.setVacationLeft(u.getVacationLeft() - days);
                BenutzerverwaltungService.updateUser(u);

            }

        }

        this.reloadAcknowledgements(actionEvent);

        absenceEvent = new AbsenceEvent();
    }

    public void onAbsenceSelect(SelectEvent selectEvent) {
        absenceEvent = (AbsenceEvent) selectEvent.getObject();

        reason = "";

        if (absenceEvent instanceof AbsenceEvent) {

            reason = absenceEvent.getAbsence().getReason();
            RequestContext.getCurrentInstance().execute("PF('eventDialog').show();");
        }

    }

    public AbsenceEvent getAbsenceEvent() {
        return absenceEvent;
    }

    public void setAbsenceEvent(AbsenceEvent event) {
        this.absenceEvent = event;
    }

    public void reloadAcknowledgements(ActionEvent event) {
        acknowledgementModel.clear();

        for (Absence a : AbsenceService.getAllUnacknowledged()) {

            AbsenceEvent e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName(), TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

            switch (e.getAbsence().getAbsenceType().getAbsenceTypeID()) {

                case 1:
                    e.setStyleClass("medical_leave");
                    break;
                case 2:
                    e.setStyleClass("holiday");
                    break;
                case 3:
                    e.setStyleClass("time_compensation");
                    break;
                case 4:
                    e.setStyleClass("business-related_absence");
                    break;

            }

            acknowledgementModel.addEvent(e);
        }

        this.selectedUser = "All";
        this.allUsers = new ArrayList<>();

        allUsers.add("All");

        for (User u : BenutzerverwaltungService.getUserList()) {
            allUsers.add(u.getUsername());
        }
    }

    public void reloadAbwesenheiten(ActionEvent event) {

        this.currentUser = BenutzerverwaltungService.getUser(currentUser.getUserNr());

        verbleibend = currentUser.getWeekTime();
        overtimeleft = currentUser.getOverTimeLeft() * 60;

        this.eventModel.clear();
        List<Absence> absenceList = AbsenceService.getAbsenceByUser(currentUser);
        DefaultScheduleEvent e;

        for (Absence a : absenceList) {
            e = new AbsenceEvent(currentUser.getUsername() + " " + a.getAbsenceType().getAbsenceName(), TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

            switch (a.getAbsenceType().getAbsenceTypeID()) {
                case 1:
                    if (a.isAcknowledged() == false) {
                        e.setStyleClass("medical_leave");
                    } else {
                        e.setStyleClass("medical_leave_acknowledged");
                    }
                    break;
                case 2:
                    if (a.isAcknowledged() == false) {
                        e.setStyleClass("holiday");
                    } else {
                        e.setStyleClass("holiday_acknowledged");
                    }
                    break;
                case 3:
                    if (a.isAcknowledged() == false) {
                        e.setStyleClass("time_compensation");
                    } else {
                        e.setStyleClass("time_compensation_acknowledged");
                    }
                    break;
                case 4:
                    if (a.isAcknowledged() == false) {
                        e.setStyleClass("business-related_absence");
                    } else {
                        e.setStyleClass("business-related_absence_acknowledged");
                    }
                    break;
            }
            this.eventModel.addEvent(e);

        }

        List<WorkTime> worktimelist = IstZeitService.getWorktimeByUser(currentUser);

        LocalDate date;
        WeekFields field;
        int currentWeek;
        int workWeek;

        date = LocalDate.now();
        field = WeekFields.of(Locale.getDefault());
        currentWeek = date.get(field.weekOfWeekBasedYear());

        int today = date.getDayOfYear();

        for (WorkTime w : worktimelist) {
            date = TimeConverterService.convertLocalDateTimeToDate(w.getStartTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            field = WeekFields.of(Locale.getDefault());
            workWeek = date.get(field.weekOfWeekBasedYear());

            eventModel.addEvent(new WorkTimeEvent(currentUser.getUsername() + " Ist-Zeit", TimeConverterService.convertLocalDateTimeToDate(w.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(w.getEndTime()), "istzeit", w));

            if (currentWeek == workWeek) {

                double time;

                LocalDateTime start = w.getStartTime();
                LocalDateTime end = w.getEndTime();

                time = start.until(end, ChronoUnit.MINUTES) / 60.0;
                time = time - w.getBreakTime() / 60.0;

                verbleibend = verbleibend - time;
            }

            if (today == date.getDayOfYear()) {
                double worktimeForToday = SollZeitenService.getSollZeitForToday(currentUser);

                LocalDateTime start = w.getStartTime();
                LocalDateTime end = w.getEndTime();

                double a = (double) end.getHour() + (double) end.getMinute() / 60;
                double b = ((double) start.getHour() + (double) start.getMinute() / 60);

                double c;
                if (w.getBreakTime() == 0) {
                    c = worktimeForToday - (a - b);
                } else {
                    c = worktimeForToday - (a - b - w.getBreakTime() / 60);
                }

                this.overtimeleft = overtimeleft - (c * 60);
            }
        }
    }

    public WorkTimeEvent getWorktimeEvent() {
        return worktimeEvent;
    }

    public void setWorktimeEvent(WorkTimeEvent worktimeEvent) {
        this.worktimeEvent = worktimeEvent;
    }

    public String getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(String selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<String> getAllUsers() {
        return allUsers;
    }

    public void loadAbwesenheitByUser(ActionEvent ev) {

        acknowledgementModel.clear();

        if ("All".equals(this.selectedUser)) {

            for (Absence a : AbsenceService.getAllUnacknowledged()) {

                AbsenceEvent e = new AbsenceEvent(a.getReason(), TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

                switch (e.getAbsence().getAbsenceType().getAbsenceTypeID()) {

                    case 1:
                        e.setStyleClass("medical_leave");
                        break;
                    case 2:
                        e.setStyleClass("holiday");
                        break;
                    case 3:
                        e.setStyleClass("time_compensation");
                        break;
                    case 4:
                        e.setStyleClass("business-related_absence");
                        break;

                }
                acknowledgementModel.addEvent(e);
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
                        break;
                    case 3:
                        e.setStyleClass("time_compensation");
                        break;
                    case 4:
                        e.setStyleClass("business-related_absence");
                        break;

                }

                acknowledgementModel.addEvent(e);
            }
        }

    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public double getVerbleibend() {
        return verbleibend;
    }

    public void setVerbleibend(double verbleibend) {
        this.verbleibend = verbleibend;
    }

    public void loadAllAbwesenheiten(ActionEvent ev) {

        this.eventModel.clear();

        AbsenceEvent e;

        for (Absence a : AbsenceService.getAllAcknowledged()) {

            e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName(), TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

            switch (e.getAbsence().getAbsenceType().getAbsenceTypeID()) {

                case 1:
                    e.setStyleClass("medical_leave_acknowledged");
                    break;
                case 2:
                    e.setStyleClass("holiday_acknowledged");
                    break;
                case 3:
                    e.setStyleClass("time_compensation_acknowledged");
                    break;
                case 4:
                    e.setStyleClass("business-related_absence_acknowledged");
                    break;

            }
            eventModel.addEvent(e);
        }

        for (Absence a : AbsenceService.getAllUnacknowledged()) {

            e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName() + " (unacknowledged)", TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

            switch (e.getAbsence().getAbsenceType().getAbsenceTypeID()) {

                case 1:
                    e.setStyleClass("medical_leave");
                    break;
                case 2:
                    e.setStyleClass("holiday");
                    break;
                case 3:
                    e.setStyleClass("time_compensation");
                    break;
                case 4:
                    e.setStyleClass("business-related_absence");
                    break;

            }
            eventModel.addEvent(e);
        }

    }

    void setUser(User user) {
        this.currentUser = user;

        verbleibend = currentUser.getWeekTime();
    }

    public String getPattern() {
        if (type.getAbsenceTypeID() == 2) {
            return "dd/MM/yyyy";
        } else {
            return "dd/MM/yyyy HH:mm";
        }
    }

    public String getStartcomment() {
        return startcomment;
    }

    public void setStartcomment(String startcomment) {
        this.startcomment = startcomment;
    }

    public String getEndcomment() {
        return endcomment;
    }

    public void setEndcomment(String endcomment) {
        this.endcomment = endcomment;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getStartDateToday() {

        LocalDateTime start = LocalDateTime.now();

        start = start.withHour(0).withMinute(0).withSecond(0).minusWeeks(1);

        return TimeConverterService.convertLocalDateTimeToDate(start);
    }

    public Date getEndDateToday() {

        LocalDateTime end = LocalDateTime.now();

        end = end.withHour(23).withMinute(59).withSecond(59);

        return TimeConverterService.convertLocalDateTimeToDate(end);
    }

    public double getOvertimeleft() {
        return overtimeleft;
    }

    public void setOvertimeleft(double overtimeleft) {
        this.overtimeleft = overtimeleft;
    }

    public void loadAllTimes(ActionEvent ev) {

        eventModel.clear();

        this.selectedUser = "All";
        this.allUsers = new ArrayList<>();

        allUsers.add("All");

        for (User u : BenutzerverwaltungService.getUserList()) {
            allUsers.add(u.getUsername());
        }

        this.loadAllAbwesenheiten(ev);

        for (WorkTime w : IstZeitService.getAllWorkTime()) {

            WorkTimeEvent e = new WorkTimeEvent(w.getUser().getUsername() + " Ist-Zeit", TimeConverterService.convertLocalDateTimeToDate(w.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(w.getEndTime()), "istzeit", w);

            eventModel.addEvent(e);
        }

    }

    public void loadAllTimesByUser(ActionEvent ev) {

        eventModel.clear();

        this.loadAbwesenheitByUserForAllTimes(ev);

        if (selectedUser.equals("All")) {
            for (WorkTime w : IstZeitService.getAllWorkTime()) {

                WorkTimeEvent e = new WorkTimeEvent(w.getUser().getUsername() + " Ist-Zeit", TimeConverterService.convertLocalDateTimeToDate(w.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(w.getEndTime()), "istzeit", w);

                eventModel.addEvent(e);
            }
        } else {
            for (WorkTime w : IstZeitService.getWorktimeByUser(BenutzerverwaltungService.getUser(selectedUser))) {
                WorkTimeEvent e = new WorkTimeEvent(w.getUser().getUsername() + " Ist-Zeit", TimeConverterService.convertLocalDateTimeToDate(w.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(w.getEndTime()), "istzeit", w);

                eventModel.addEvent(e);
            }
        }
    }

    public void loadAbwesenheitByUserForAllTimes(ActionEvent ev) {

        if ("All".equals(this.selectedUser)) {

            for (Absence a : AbsenceService.getAllAcknowledged()) {

                AbsenceEvent e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName(), TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

                switch (e.getAbsence().getAbsenceType().getAbsenceTypeID()) {

                    case 1:
                        e.setStyleClass("medical_leave_acknowledged");
                        break;
                    case 2:
                        e.setStyleClass("holiday_acknowledged");
                        break;
                    case 3:
                        e.setStyleClass("time_compensation_acknowledged");
                        break;
                    case 4:
                        e.setStyleClass("business-related_absence_acknowledged");
                        break;

                }
                eventModel.addEvent(e);
            }

            for (Absence a : AbsenceService.getAllUnacknowledged()) {

                AbsenceEvent e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName() + " (unacknowledged)", TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

                switch (e.getAbsence().getAbsenceType().getAbsenceTypeID()) {

                    case 1:
                        e.setStyleClass("medical_leave");
                        break;
                    case 2:
                        e.setStyleClass("holiday");
                        break;
                    case 3:
                        e.setStyleClass("time_compensation");
                        break;
                    case 4:
                        e.setStyleClass("business-related_absence");
                        break;

                }
                eventModel.addEvent(e);
            }
        } else {

            for (Absence a : AbsenceService.getAbsenceByUserAndUnacknowledged(BenutzerverwaltungService.getUser(selectedUser))) {

                AbsenceEvent e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName() + " (unacknowledged)", TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

                switch (e.getAbsence().getAbsenceType().getAbsenceTypeID()) {

                    case 1:
                        e.setStyleClass("medical_leave");
                        break;
                    case 2:
                        e.setStyleClass("holiday");
                        break;
                    case 3:
                        e.setStyleClass("time_compensation");
                        break;
                    case 4:
                        e.setStyleClass("business-related_absence");
                        break;

                }

                eventModel.addEvent(e);
            }

            for (Absence a : AbsenceService.getAbsenceByUserAndAcknowledged(BenutzerverwaltungService.getUser(selectedUser))) {

                AbsenceEvent e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName(), TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()), a);

                switch (e.getAbsence().getAbsenceType().getAbsenceTypeID()) {

                    case 1:
                        e.setStyleClass("medical_leave_acknowledged");
                        break;
                    case 2:
                        e.setStyleClass("holiday_acknowledged");
                        break;
                    case 3:
                        e.setStyleClass("time_compensation_acknowledged");
                        break;
                    case 4:
                        e.setStyleClass("business-related_absence_acknowledged");
                        break;

                }

                eventModel.addEvent(e);
            }
        }

    }
}
