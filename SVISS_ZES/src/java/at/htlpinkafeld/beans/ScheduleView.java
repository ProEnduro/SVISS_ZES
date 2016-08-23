/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.beans.util.AbsenceEvent;
import at.htlpinkafeld.beans.util.LazyScheduleModels.AcknowledgeLazyScheduleModel;
import at.htlpinkafeld.beans.util.LazyScheduleModels.AllTimeLazyScheduleModel;
import at.htlpinkafeld.beans.util.LazyScheduleModels.AlleAbwesenheitenLazyScheduleModel;
import at.htlpinkafeld.beans.util.LazyScheduleModels.IstZeitLazyScheduleModel;
import at.htlpinkafeld.beans.util.WorkTimeEvent;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.AbsenceType;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.AccessRightsService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.EmailService;
import at.htlpinkafeld.service.IstZeitService;
import at.htlpinkafeld.service.SollZeitenService;
import at.htlpinkafeld.service.TimeConverterService;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

public class ScheduleView implements Serializable {

    AbsenceType type;
    List<AbsenceType> types;
    User currentUser;

    private ScheduleModel eventModel;

    private AcknowledgeLazyScheduleModel acknowledgementModel;
    private AllTimeLazyScheduleModel allTimesModel;
    private AlleAbwesenheitenLazyScheduleModel alleAbwesenheitenModel;

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

        types = AbsenceService.getList();
        type = types.get(0);

        FacesContext context = FacesContext.getCurrentInstance();
        MasterBean masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);
        currentUser = masterBean.getUser();

        eventModel = new IstZeitLazyScheduleModel(currentUser);
        acknowledgementModel = new AcknowledgeLazyScheduleModel();
        allTimesModel = new AllTimeLazyScheduleModel();
        alleAbwesenheitenModel = new AlleAbwesenheitenLazyScheduleModel();

        this.reloadAbwesenheiten();
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
            List<User> approverlist = currentUser.getApprover();
            if (!approverlist.isEmpty()) {
                EmailService.sendUserEnteredAbsenceEmail(a, approverlist);
            }
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
                    EmailService.sendUserDeletedOwnAbsenceEmail(absenceEvent.getAbsence(), absenceEvent.getAbsence().getUser().getApprover());
                } else {
                    FacesContext context = FacesContext.getCurrentInstance();

                    context.addMessage(null, new FacesMessage("Failed", "You can't delete acknowledged absences!"));
                }
            }
        }

    }

    public void addIstZeitEvent(ActionEvent actionEvent) {
        int diff = 0;

        LocalDateTime startDT = TimeConverterService.convertDateToLocalDate(event.getStartDate()).atStartOfDay();
        List<WorkTime> workTimes = IstZeitService.getWorkTimeForUserBetweenStartAndEndDate(currentUser, TimeConverterService.convertLocalDateTimeToDate(startDT), TimeConverterService.convertLocalDateTimeToDate(startDT.plusDays(1)));
        if (event.getStartDate().after(event.getEndDate())) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage("Endzeitpunkt ist vor Startzeitpunkt!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else {

            if (!workTimes.isEmpty() && event.getId() == null) {
                FacesContext.getCurrentInstance().addMessage("", new FacesMessage("Nur eine Arbeitszeit pro Tag!"));
                FacesContext.getCurrentInstance().validationFailed();
            } else if (event.getId() == null) {
                WorkTimeEvent e = new WorkTimeEvent(this.currentUser.getUsername() + " " + "Ist-Zeit", event.getStartDate(), event.getEndDate(), new WorkTime(currentUser, TimeConverterService.convertDateToLocalDateTime(this.event.getStartDate()), TimeConverterService.convertDateToLocalDateTime(this.event.getEndDate()), 0, "", ""));

                e.getWorktime().setStartComment(startcomment);
                e.getWorktime().setEndComment(endcomment);
                e.getWorktime().setBreakTime(breaktime);

                e.setStyleClass("istzeit");
                IstZeitService.addIstTime(e.getWorktime());

            } else {

                if (event instanceof WorkTimeEvent) {
                    WorkTime time = ((WorkTimeEvent) event).getWorktime();
                    LocalTime plus19 = LocalTime.of(19, 0);
                    if (time.getEndTime().toLocalTime().isAfter(plus19)) {
                        diff -= time.getOvertimeAfter19() * 1.5;
                        diff -= time.getStartTime().until(plus19, ChronoUnit.MINUTES);
                    } else {
                        diff -= time.getStartTime().until(time.getEndTime(), ChronoUnit.MINUTES);
                    }
                    time.setStartTime(TimeConverterService.convertDateToLocalDateTime(event.getStartDate()));
                    time.setEndTime(TimeConverterService.convertDateToLocalDateTime(event.getEndDate()));

                    IstZeitService.update(time);
                } else if (event instanceof AbsenceEvent) {

//                AbsenceService.updateAbsence(((AbsenceEvent) event).getAbsence());
                }
            }

            if (event instanceof WorkTimeEvent) {

                WorkTime wt = ((WorkTimeEvent) event).getWorktime();

                LocalTime plus19 = LocalTime.of(19, 0);
                if (wt.getEndTime().toLocalTime().isAfter(plus19)) {
                    diff += wt.getOvertimeAfter19() * 1.5;
                    diff += wt.getStartTime().until(plus19, ChronoUnit.MINUTES);
                } else {
                    diff += wt.getStartTime().until(wt.getEndTime(), ChronoUnit.MINUTES);
                }
                wt.getUser().setOverTimeLeft(wt.getUser().getOverTimeLeft() + diff);
                BenutzerverwaltungService.updateUser(wt.getUser());
            }
            event = new DefaultScheduleEvent();
        }
    }

    public void deleteIstZeitEvent(ActionEvent e) {
        if (event.getId() != null) {
            if (event instanceof WorkTimeEvent) {
                eventModel.deleteEvent(event);
                WorkTimeEvent workevent = (WorkTimeEvent) event;
                IstZeitService.delete(workevent.getWorktime());

                int diff = 0;
                WorkTime time = workevent.getWorktime();
                LocalTime plus19 = LocalTime.of(19, 0);
                if (time.getEndTime().toLocalTime().isAfter(plus19)) {
                    diff -= time.getOvertimeAfter19() * 1.5;
                    diff -= time.getStartTime().until(plus19, ChronoUnit.MINUTES);
                } else {
                    diff -= time.getStartTime().until(time.getEndTime(), ChronoUnit.MINUTES);
                }
                time.getUser().setOverTimeLeft(time.getUser().getOverTimeLeft() + diff);
                BenutzerverwaltungService.updateUser(time.getUser());

                event = new DefaultScheduleEvent();
            }
        }
    }

    public void onIstZeitEventSelect(SelectEvent selectEvent) {
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
        } else {
            RequestContext.getCurrentInstance().execute("PF('feiertagDialog').show();");
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
        } else {
            RequestContext.getCurrentInstance().execute(("PF('feiertagDialog').show();"));
        }
    }

    public void onIstZeitDateSelect(SelectEvent selectEvent) {
        Date sDate=(Date) selectEvent.getObject();
        if(sDate.before(getStartDateToday())|| sDate.after(getEndDateToday())){
            FacesContext.getCurrentInstance().validationFailed();
        }
            
        event = new DefaultScheduleEvent("", sDate, sDate);
    }
    
        public void onAbsenceDateSelect(SelectEvent selectEvent) {
        Date sDate=(Date) selectEvent.getObject();
        event = new DefaultScheduleEvent("", sDate, sDate);
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

        } else if (currentUser.getUsername().equals(absenceEvent.getAbsence().getUser().getUsername())) {

            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Absence-acknowledge failed", "You can't acknowledge your own abscences!"));

        } else {

            acknowledgementModel.deleteEvent(absenceEvent);

            absenceEvent.getAbsence().setAcknowledged(true);
            AbsenceService.updateAbsence(absenceEvent.getAbsence());

            User u = absenceEvent.getAbsence().getUser();

            int overtime = calcAbsenceOvertime(absenceEvent.getAbsence());

            u.setOverTimeLeft(u.getOverTimeLeft() + overtime);
            BenutzerverwaltungService.updateUser(u);

            List<User> otherApprover = absenceEvent.getAbsence().getUser().getApprover();
            otherApprover.remove(currentUser);
            EmailService.sendAcknowledgmentEmail(absenceEvent.getAbsence(), currentUser, otherApprover);

        }

        this.reloadAcknowledgements();

        absenceEvent = new AbsenceEvent();
    }

    public void deleteAcknowledgement(ActionEvent e) {
        if (absenceEvent.getId() == null) {

        } else {
            acknowledgementModel.deleteEvent(absenceEvent);
            AbsenceService.deleteAbsence(absenceEvent.getAbsence());

            if (absenceEvent.getAbsence().isAcknowledged()) {
                User u = absenceEvent.getAbsence().getUser();
                int overtime = calcAbsenceOvertime(absenceEvent.getAbsence());

                u.setOverTimeLeft(u.getOverTimeLeft() - overtime);
                BenutzerverwaltungService.updateUser(u);
            }

            List<User> otherApprover = absenceEvent.getAbsence().getUser().getApprover();
            otherApprover.remove(currentUser);
            EmailService.sendAbsenceDeletedByApprover(absenceEvent.getAbsence(), currentUser, otherApprover);

        }
    }

    private int calcAbsenceOvertime(Absence a) {
        int overtime = 0;
        User u = a.getUser();
        List<SollZeit> sollZeiten = SollZeitenService.getSollZeitenByUser(u);
        int days = 0;
        DayOfWeek sDay;

        switch (absenceEvent.getAbsence().getAbsenceType().getAbsenceName()) {
            case "holiday":
                days = (int) (a.getStartTime().until(a.getEndTime(), ChronoUnit.DAYS) + 1);

                u.setVacationLeft(u.getVacationLeft() - days);
                BenutzerverwaltungService.updateUser(u);

                DayOfWeek hDay = a.getStartTime().getDayOfWeek();
                for (int i = 0; i < days; i++, hDay.plus(1)) {
                    for (SollZeit sz : sollZeiten) {
                        if (sz.getDay().equals(hDay)) {
                            int diff = (int) sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                            if (diff > 6 * 60) {
                                diff -= 30;
                            }
                            overtime += diff;
                        }
                    }
                }

                break;
            case "time compensation":
                days = (int) (a.getStartTime().until(a.getEndTime(), ChronoUnit.DAYS) + 1);
                sDay = a.getStartTime().getDayOfWeek();

                for (int i = 0; i < days; i++, sDay.plus(1)) {
                    for (SollZeit sz : sollZeiten) {
                        if (sz.getDay().equals(sDay)) {
                            int diff = 0;
                            if (i == 0 || i == (days - 1)) {
                                if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime()) && a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime())) {
                                    diff = (int) a.getStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
                                } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollStartTime()) || a.getStartTime().toLocalTime().isAfter(sz.getSollEndTime())) {
                                } else if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime())) {
                                    diff = (int) a.getStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                                } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime())) {
                                    diff = (int) sz.getSollStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
                                } else {
                                    diff = (int) sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                                }
                            } else {
                                diff = (int) sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                            }
                            if (diff > 6 * 60) {
                                diff -= 30;
                            }
                            overtime -= diff;
                        }
                    }
                }
                break;
            case "medical leave":
                days = (int) (a.getStartTime().until(a.getEndTime(), ChronoUnit.DAYS) + 1);
                sDay = a.getStartTime().getDayOfWeek();

                for (int i = 0; i < days; i++, sDay.plus(1)) {
                    for (SollZeit sz : sollZeiten) {
                        if (sz.getDay().equals(sDay)) {
                            int diff = 0;
                            if (i == 0 || i == (days - 1)) {
                                if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime()) && a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime())) {
                                    diff = (int) a.getStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
                                } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollStartTime()) || a.getStartTime().toLocalTime().isAfter(sz.getSollEndTime())) {
                                } else if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime())) {
                                    diff = (int) a.getStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                                } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime())) {
                                    diff = (int) sz.getSollStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
                                } else {
                                    diff = (int) sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                                }
                            } else {
                                diff = (int) sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                            }
                            if (diff > 6 * 60) {
                                diff -= 30;
                            }
                            overtime += diff;
                        }
                    }
                }
                break;
        }
        return overtime;
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

    public String reloadAcknowledgements() {
        this.selectedUser = "All";
        this.allUsers = new ArrayList<>();

        allUsers.add("All");

        for (User u : BenutzerverwaltungService.getUserList()) {
            allUsers.add(u.getUsername());
        }

        return "/pages/absence_acknowledgement.xhtml?faces-redirect=true";
    }

    public String reloadAbwesenheiten() {

        this.currentUser = BenutzerverwaltungService.getUser(currentUser.getUserNr());

        verbleibend = currentUser.getWeekTime();
        overtimeleft = currentUser.getOverTimeLeft();

        return "/pages/istzeit.xhtml?faces-redirect=true";
    }

    public ScheduleModel getAlleAbwesenheitenModel() {
        return alleAbwesenheitenModel;
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
        this.acknowledgementModel.setSelectedUser(this.selectedUser);
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

    public ScheduleModel getAllTimesModel() {
        return allTimesModel;
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

    public String loadAllTimes() {
        this.selectedUser = "All";
        this.allUsers = new ArrayList<>();

        allUsers.add("All");

        for (User u : BenutzerverwaltungService.getUserList()) {
            allUsers.add(u.getUsername());
        }

        return "/pages/allTime.xhtml?faces-redirect=true";
    }

    public void loadAllTimesByUser(ActionEvent ev) {

        allTimesModel.setSelectedUser(this.getSelectedUser());
    }

    public boolean isAcknowledgedAbsence() {
        if (AccessRightsService.checkPermission(this.currentUser.getAccessLevel(), "ALL")) {
            return true;
        }
        return false;
    }

    public void deleteEventInAllTime(ActionEvent e) {
        AbsenceEvent absenceevent = (AbsenceEvent) event;

        this.eventModel.deleteEvent(absenceevent);
        AbsenceService.deleteAbsence(absenceevent.getAbsence());
    }

    public String redirectToAbwesenheiten() {
        return "/pages/abwesenheit.xhtml?faces-redirect=true";
    }

    public String redirectToAccountverwaltung() {
        return "/pages/benutzerkonto.xhtml?faces-redirect=true";
    }

    public String redirectToBenutzerverwaltung() {
        return "/pages/benutzerverwaltung.xhtml?faces-redirect=true";
    }

    public String redirectToFeiertage() {
        return "/pages/holidays.xhtml?faces-redirect=true";
    }

    public String redirectToAllAbwesenheiten() {
        return "/pages/view_all_abwesenheiten.xhtml?faces-redirect=true";
    }
}
