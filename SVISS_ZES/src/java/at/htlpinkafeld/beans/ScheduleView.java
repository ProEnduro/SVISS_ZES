/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.beans.util.AbsenceEvent;
import at.htlpinkafeld.beans.util.WorkTimeEvent;
import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.AbsenceType;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.IstZeitService;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ScheduleView implements Serializable {

    public static int istzeit = 1;

    int type = istzeit;
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

    String startcomment;
    String endcomment;
    String reason;

    int breaktime;

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

    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();

//        eventModel.addEvent(new DefaultScheduleEvent("Champions League Match", previousDay8Pm(), previousDay11Pm(), "emp1"));
//        eventModel.addEvent(new DefaultScheduleEvent("Birthday Party", today1Pm(), today6Pm()));
//        eventModel.addEvent(new DefaultScheduleEvent("Breakfast at Tiffanys", nextDay9Am(), nextDay11Am()));
//        eventModel.addEvent(new DefaultScheduleEvent("Plant the new garden stuff", theDayAfter3Pm(), fourDaysLater3pm()));
        acknowledgementModel = new DefaultScheduleModel();

        types = DAOFactory.getDAOFactory().getAbsenceTypeDAO().getList();

        FacesContext context = FacesContext.getCurrentInstance();
        MasterBean masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);
        currentUser = masterBean.getUser();

//        List<WorkTime> worklist = DAOFactory.getDAOFactory().getWorkTimeDAO().getWorkTimesByUser(currentUser);
//
//        for (WorkTime w : worklist) {
//            System.out.println(w);
//            eventModel.addEvent(new DefaultScheduleEvent("test 1", w.getStartTime(), w.getEndTime(), "istzeit"));
//        }
        this.reloadAbwesenheiten(null);

    }

    public Date getRandomDate(Date base) {
        Calendar date = Calendar.getInstance();
        date.setTime(base);
        date.add(Calendar.DATE, ((int) (Math.random() * 30)) + 1);    //set random day of month

        return date.getTime();
    }

    public Date getInitialDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar.getTime();
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public ScheduleModel getAcknowlegementModel() {
        return acknowledgementModel;
    }

    private Calendar today() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar;
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

            Absence a = new Absence(this.currentUser, types.get(type - 1), IstZeitService.convertDateToLocalDateTime(e.getStartDate()), IstZeitService.convertDateToLocalDateTime(e.getEndDate()));
            a.setReason(this.reason);

            AbsenceEvent ev = new AbsenceEvent(currentUser.getUsername() + " " + types.get(type - 1).getAbsenceName(), event.getStartDate(), event.getEndDate(), a);

            switch (this.type) {
                case 1:
                    ev.setStyleClass(types.get(type - 1).getAbsenceName().replace(" ", "_"));

                    DAOFactory.getDAOFactory().getAbsenceDAO().insert(a);
                    break;
                case 2:
                    ev.setStyleClass(types.get(type - 1).getAbsenceName().replace(" ", "_"));

                    LocalDateTime time = IstZeitService.convertDateToLocalDateTime(ev.getEndDate());
                    Date d = IstZeitService.convertLocalDateTimeToDate(time.plusMinutes(1439));
                    ev.setEndDate(d);

                    DAOFactory.getDAOFactory().getAbsenceDAO().insert(a);
                    break;
                case 3:
                    ev.setStyleClass("time_compensation");

                    DAOFactory.getDAOFactory().getAbsenceDAO().insert(a);
                    break;
                case 4:
                    ev.setStyleClass("business-related_absence");

                    DAOFactory.getDAOFactory().getAbsenceDAO().insert(a);
                    break;
            }

            eventModel.addEvent(ev);
//            IstZeitService.addIstTime(currentUser, e.getStartDate(),e.getEndDate());
        } else {

            if (event instanceof WorkTimeEvent) {
                IstZeitService.update(((WorkTimeEvent) event).getWorktime());
            } else if (event instanceof AbsenceEvent) {
                AbsenceService.updateAbsence(((AbsenceEvent) event).getAbsence());
            }

            eventModel.updateEvent(event);
        }

//        for(WorkTime w: IstZeitService.getAllWorkTime()){
//            System.out.println(w);
//        }
        event = new DefaultScheduleEvent();
    }

    public void addIstZeitEvent(ActionEvent actionEvent) {
        if (event.getId() == null) {
            WorkTimeEvent e = new WorkTimeEvent(this.currentUser.getUsername() + " " + "Ist-Zeit", event.getStartDate(), event.getEndDate(), new WorkTime(currentUser, IstZeitService.convertDateToLocalDateTime(this.event.getStartDate()), IstZeitService.convertDateToLocalDateTime(this.event.getEndDate()), 0, "", ""));

            e.getWorktime().setStartComment(startcomment);
            e.getWorktime().setEndComment(endcomment);
            e.getWorktime().setBreakTime(breaktime);

            e.setStyleClass("istzeit");
            IstZeitService.addIstTime(e.getWorktime());

            eventModel.addEvent(e);

        } else {

            if (event instanceof WorkTimeEvent) {
                WorkTime time = ((WorkTimeEvent) event).getWorktime();

                time.setStartTime(IstZeitService.convertDateToLocalDateTime(event.getStartDate()));
                time.setEndTime(IstZeitService.convertDateToLocalDateTime(event.getEndDate()));

                IstZeitService.update(time);
            } else if (event instanceof AbsenceEvent) {

                AbsenceService.updateAbsence(((AbsenceEvent) event).getAbsence());
            }

            eventModel.updateEvent(event);
        }

        event = new DefaultScheduleEvent();
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

    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<AbsenceType> getTypes() {
        return types;
    }

    public void setTypes(List<AbsenceType> types) {
        this.types = types;
    }

    public void radioAjax() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(types.get(type - 1).getAbsenceName()));
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

                int days = lae.getDayOfYear() - las.getDayOfYear();

                if (days == 0) {
                    User u = absenceEvent.getAbsence().getUser();
                    u.setVacationLeft(u.getVacationLeft() - 1);
                    BenutzerverwaltungService.updateUser(u);
                } else {
                    User u = absenceEvent.getAbsence().getUser();

                    u.setVacationLeft(u.getVacationLeft() - ++days);
                    BenutzerverwaltungService.updateUser(u);
                }
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

            AbsenceEvent e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName(), IstZeitService.convertLocalDateTimeToDate(a.getStartTime()), IstZeitService.convertLocalDateTimeToDate(a.getEndTime()), a);

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
//        verbleibend = currentUser.getWeekTime();

        verbleibend = currentUser.getWeekTime();

        this.eventModel.clear();
        List<Absence> absenceList = AbsenceService.getAbsenceByUser(currentUser);
        DefaultScheduleEvent e;

        for (Absence a : absenceList) {
            e = new AbsenceEvent(currentUser.getUsername() + " " + a.getAbsenceType().getAbsenceName(), IstZeitService.convertLocalDateTimeToDate(a.getStartTime()), IstZeitService.convertLocalDateTimeToDate(a.getEndTime()), a);

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
            e.setEditable(false);
            this.eventModel.addEvent(e);

        }

        List<WorkTime> worktimelist = IstZeitService.getWorktimeByUser(currentUser);

        LocalDate date;
        WeekFields field;
        int currentWeek;
        int workWeek;

        date = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        field = WeekFields.of(Locale.getDefault());
        currentWeek = date.get(field.weekOfWeekBasedYear());

        for (WorkTime w : worktimelist) {
            date = IstZeitService.convertLocalDateTimeToDate(w.getStartTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            field = WeekFields.of(Locale.getDefault());
            workWeek = date.get(field.weekOfWeekBasedYear());

            eventModel.addEvent(new WorkTimeEvent(currentUser.getUsername() + " Ist-Zeit", IstZeitService.convertLocalDateTimeToDate(w.getStartTime()), IstZeitService.convertLocalDateTimeToDate(w.getEndTime()), "istzeit", w));

            if (currentWeek == workWeek) {

                double time;

                LocalDateTime start = w.getStartTime();
                LocalDateTime end = w.getEndTime();

                int sh = start.getHour();
                double sm = start.getMinute() / 60;
                double st = sh + sm;

                double eh = end.getHour();
                double em = end.getMinute() / 60;
                double et = eh + em;

                time = et - st;
                time = time - ((double) w.getBreakTime() / 60);

                verbleibend = verbleibend - time;
            }
        }
    }

    public WorkTimeEvent getWorktimeEvent() {
        return worktimeEvent;
    }

    public void setWorktimeEvent(WorkTimeEvent worktimeEvent) {
        this.worktimeEvent = worktimeEvent;
    }

    public void onWorkTimeEventSelect(SelectEvent selectEvent) {
        if (selectEvent.getObject() instanceof WorkTimeEvent) {
            this.worktimeEvent = (WorkTimeEvent) selectEvent.getObject();
            event = worktimeEvent;
        } else if (selectEvent.getObject() instanceof AbsenceEvent) {
            this.absenceEvent = (AbsenceEvent) selectEvent.getObject();
            event = absenceEvent;
        }
    }

    public void onWorkTimeMove(ScheduleEntryMoveEvent event) {

//        System.out.println(event.getScheduleEvent().getClass());
//        this.worktimeEvent = (WorkTimeEvent)event.getScheduleEvent();
//        System.out.println(worktimeEvent.getWorktime());
//        
        if (event.getScheduleEvent() instanceof WorkTimeEvent) {
            IstZeitService.update(((WorkTimeEvent) event.getScheduleEvent()).getWorktime());
        } else if (event.getScheduleEvent() instanceof AbsenceEvent) {
            AbsenceService.updateAbsence(((AbsenceEvent) event.getScheduleEvent()).getAbsence());
        }
    }

    public void onWorkTimeResize(ScheduleEntryResizeEvent event) {

        if (event.getScheduleEvent() instanceof WorkTimeEvent) {
            IstZeitService.update(((WorkTimeEvent) event.getScheduleEvent()).getWorktime());
        } else if (event.getScheduleEvent() instanceof AbsenceEvent) {
            AbsenceService.updateAbsence(((AbsenceEvent) event.getScheduleEvent()).getAbsence());
        }
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

    public void setAllUsers(List<String> allUsers) {
        this.allUsers = allUsers;
    }

    public void loadAbwesenheitByUser(ActionEvent ev) {

        acknowledgementModel.clear();

        if ("All".equals(this.selectedUser)) {

            for (Absence a : AbsenceService.getAllUnacknowledged()) {

                AbsenceEvent e = new AbsenceEvent(a.getReason(), IstZeitService.convertLocalDateTimeToDate(a.getStartTime()), IstZeitService.convertLocalDateTimeToDate(a.getEndTime()), a);

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

                AbsenceEvent e = new AbsenceEvent(a.getReason(), IstZeitService.convertLocalDateTimeToDate(a.getStartTime()), IstZeitService.convertLocalDateTimeToDate(a.getEndTime()), a);

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

            e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName(), IstZeitService.convertLocalDateTimeToDate(a.getStartTime()), IstZeitService.convertLocalDateTimeToDate(a.getEndTime()), a);

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

            e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName() + " (unacknowledged)", IstZeitService.convertLocalDateTimeToDate(a.getStartTime()), IstZeitService.convertLocalDateTimeToDate(a.getEndTime()), a);

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

        if (type == 2) {
            return "dd/MM/yyyy";
        } else {
            return "dd/MM/yyyy HH:mm";
        }

    }

    public void setPattern(String pat) {

    }

    public boolean getHouronly() {
        return false;
    }

    public void setHouronly(boolean b) {

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

            WorkTimeEvent e = new WorkTimeEvent(w.getUser().getUsername() + " Ist-Zeit", IstZeitService.convertLocalDateTimeToDate(w.getStartTime()), IstZeitService.convertLocalDateTimeToDate(w.getEndTime()), "istzeit", w);

            eventModel.addEvent(e);
        }

    }

    public void loadAllTimesByUser(ActionEvent ev) {

        eventModel.clear();

        this.loadAbwesenheitByUserForAllTimes(ev);

        if (selectedUser.equals("All")) {
            for (WorkTime w : IstZeitService.getAllWorkTime()) {

                WorkTimeEvent e = new WorkTimeEvent(w.getUser().getUsername() + " Ist-Zeit", IstZeitService.convertLocalDateTimeToDate(w.getStartTime()), IstZeitService.convertLocalDateTimeToDate(w.getEndTime()), "istzeit", w);

                eventModel.addEvent(e);
            }
        } else {
            for (WorkTime w : IstZeitService.getWorktimeByUser(BenutzerverwaltungService.getUser(selectedUser))) {
                WorkTimeEvent e = new WorkTimeEvent(w.getUser().getUsername() + " Ist-Zeit", IstZeitService.convertLocalDateTimeToDate(w.getStartTime()), IstZeitService.convertLocalDateTimeToDate(w.getEndTime()), "istzeit", w);

                eventModel.addEvent(e);
            }
        }
    }

    public void loadAbwesenheitByUserForAllTimes(ActionEvent ev) {

        if ("All".equals(this.selectedUser)) {

            for (Absence a : AbsenceService.getAllAcknowledged()) {

                AbsenceEvent e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName(), IstZeitService.convertLocalDateTimeToDate(a.getStartTime()), IstZeitService.convertLocalDateTimeToDate(a.getEndTime()), a);

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

                AbsenceEvent e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName() + " (unacknowledged)", IstZeitService.convertLocalDateTimeToDate(a.getStartTime()), IstZeitService.convertLocalDateTimeToDate(a.getEndTime()), a);

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

                AbsenceEvent e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName() + " (unacknowledged)", IstZeitService.convertLocalDateTimeToDate(a.getStartTime()), IstZeitService.convertLocalDateTimeToDate(a.getEndTime()), a);

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

                AbsenceEvent e = new AbsenceEvent(a.getUser().getUsername() + " " + a.getAbsenceType().getAbsenceName(), IstZeitService.convertLocalDateTimeToDate(a.getStartTime()), IstZeitService.convertLocalDateTimeToDate(a.getEndTime()), a);

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

    // TODO: Emails senden -> first Tests
    public void sendEmail(ActionEvent e) throws AddressException, MessagingException {

        Properties mailServerProperties;
        Session getMailSession;
        MimeMessage generateMailMessage;

        // Step1
        System.out.println("\n 1st ===> setup Mail Server Properties..");
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");
        System.out.println("Mail Server Properties have been setup successfully..");

        // Step2
        System.out.println("\n\n 2nd ===> get Mail Session..");
        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        generateMailMessage = new MimeMessage(getMailSession);
        generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("ma.six98@gmail.com"));
        generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("blaumeux.sn@gmail.com"));
        generateMailMessage.setSubject("Greetings from Crunchify..");
        String emailBody = "Test email by Crunchify.com JavaMail API example. " + "<br><br> Regards, <br>Crunchify Admin";
        generateMailMessage.setContent(emailBody, "text/html");
        System.out.println("Mail Session has been created successfully..");

        // Step3
        System.out.println("\n\n 3rd ===> Get Session and Send mail");
        Transport transport = getMailSession.getTransport("smtp");

        // Enter your correct gmail UserID and Password
        // if you have 2FA enabled then provide App Specific Password
        String user = "blaumeux.sn@gmail.com";
        String password = "password";

        transport.connect("smtp.gmail.com", user, password);
        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
        transport.close();

    }
    
    public Date getStartDateToday(){
        
        LocalDateTime start = LocalDateTime.now();
        
        start = start.withHour(0).withMinute(0).withSecond(0).minusWeeks(1);
            
        return IstZeitService.convertLocalDateTimeToDate(start);
    }
    
    public Date getEndDateToday(){
        
        LocalDateTime end = LocalDateTime.now();
        
        end = end.withHour(23).withMinute(59).withSecond(59);
        
        
        return IstZeitService.convertLocalDateTimeToDate(end);
    }

}
