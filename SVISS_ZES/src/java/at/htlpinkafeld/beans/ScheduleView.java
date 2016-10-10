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
import at.htlpinkafeld.dao.util.DAODML_Observer;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.AbsenceType;
import at.htlpinkafeld.pojo.Holiday;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.AccessRightsService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.EmailService;
import at.htlpinkafeld.service.HolidayService;
import at.htlpinkafeld.service.IstZeitService;
import at.htlpinkafeld.service.SollZeitenService;
import at.htlpinkafeld.service.TimeConverterService;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
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

public class ScheduleView implements Serializable, DAODML_Observer {

    AbsenceType type;
    List<AbsenceType> types;
    User currentUser;

    private ScheduleModel istZeitEventModel;

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

        FacesContext context = FacesContext.getCurrentInstance();
        MasterBean masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);
        currentUser = masterBean.getUser();
        if (currentUser != null) {
            BenutzerverwaltungService.addUserObserver(this);

            types = AbsenceService.getList();
            type = types.get(0);

            istZeitEventModel = new IstZeitLazyScheduleModel(currentUser);
            acknowledgementModel = new AcknowledgeLazyScheduleModel();
            allTimesModel = new AllTimeLazyScheduleModel();
            alleAbwesenheitenModel = new AlleAbwesenheitenLazyScheduleModel();

            this.reloadAbwesenheiten();
        }
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

    public ScheduleModel getIstZeitEventModel() {
        return istZeitEventModel;
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

    public void addAbsenceEvent(ActionEvent actionEvent) {

        LocalDateTime startDT = TimeConverterService.convertDateToLocalDateTime(event.getStartDate());
        LocalDateTime endDT = TimeConverterService.convertDateToLocalDateTime(event.getEndDate());

        if ((!startDT.toLocalDate().equals(endDT.toLocalDate()) && endDT.toLocalTime().equals(LocalTime.MIN)) || startDT.equals(endDT)) {
            startDT = startDT.with(LocalTime.MIN);
            endDT = endDT.with(LocalTime.of(23, 59, 59));
        }

        if (event.getStartDate().after(event.getEndDate())) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Endzeitpunkt ist vor Startzeitpunkt!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else if (!checkAvailableTime(TimeConverterService.convertLocalDateTimeToDate(startDT), TimeConverterService.convertLocalDateTimeToDate(endDT), true)) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Ein Eintrag für diese Urzeit ist bereits vorhanden!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else if (type.getAbsenceName().equals("holiday")) {
            int days = 0;
            List<SollZeit> sollZeiten = SollZeitenService.getSollZeitenByUser(currentUser);
            for (LocalDate ld = startDT.toLocalDate(); ld.atStartOfDay().isBefore(endDT); ld = ld.plusDays(1)) {
                for (SollZeit sz : sollZeiten) {
                    if (sz.getDay().equals(ld.getDayOfWeek())) {
                        days++;
                    }
                }
            }
            if (days > currentUser.getVacationLeft()) {
                FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Sie haben nicht genug Urlaub übrig!"));
                FacesContext.getCurrentInstance().validationFailed();
            } else if (event instanceof AbsenceEvent && event.getId() == null) {

                Absence a = ((AbsenceEvent) event).getAbsence();
                a.setStartTime(startDT);
                a.setEndTime(endDT);
                a.setAbsenceType(type);
                a.setReason(this.reason);

                AbsenceService.insertAbsence(a);
                List<User> approverlist = currentUser.getApprover();
                if (!approverlist.isEmpty()) {
                    EmailService.sendUserEnteredAbsenceEmail(a, approverlist);
                }
            } else if (event instanceof WorkTimeEvent) {
                IstZeitService.update(((WorkTimeEvent) event).getWorktime());
            } else if (event instanceof AbsenceEvent) {
                Absence a = ((AbsenceEvent) event).getAbsence();
                a.setReason(reason);
                a.setStartTime(startDT);
                a.setEndTime(endDT);
                AbsenceService.updateAbsence(a);
            }

            event = new DefaultScheduleEvent();
        }
    }

    public boolean isAbleToDeleteAbsence() {
        return AccessRightsService.checkPermission(currentUser.getAccessLevel(), "ALL");
    }

    public void deleteAbsenceEvent() {

        if (event.getId() != null) {
            if (event instanceof AbsenceEvent) {
                AbsenceEvent absenceEvent = (AbsenceEvent) event;
                if (absenceEvent.getAbsence().isAcknowledged() == false) {
                    AbsenceService.removeAbsence(absenceEvent.getAbsence());
//                    istZeitEventModel.deleteEvent(event);
//                    event = new DefaultScheduleEvent();
                    EmailService.sendUserDeletedOwnAbsenceEmail(absenceEvent.getAbsence(), absenceEvent.getAbsence().getUser().getApprover());
                } else {
                    FacesContext context = FacesContext.getCurrentInstance();

                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "You can't delete acknowledged absences!"));
                }
            }
        }

    }

    public void addIstZeitEvent(ActionEvent actionEvent) {
        int diff = 0;

        LocalDateTime startDT = TimeConverterService.convertDateToLocalDateTime(event.getStartDate());
        LocalDateTime endDT = TimeConverterService.convertDateToLocalDateTime(event.getEndDate());

        WorkTime wt = null;

        List<WorkTime> workTimes = IstZeitService.getWorkTimeForUserBetweenStartAndEndDate(currentUser, TimeConverterService.convertLocalDateTimeToDate(startDT.with(LocalTime.MIN)), TimeConverterService.convertLocalDateTimeToDate(startDT.with(LocalTime.MIN).plusDays(1)));

        if (event.getStartDate().after(event.getEndDate())) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Endzeitpunkt ist vor Startzeitpunkt!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else if (!startDT.toLocalDate().equals(endDT.toLocalDate())) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Arbeitszeit kann nur an einem Tag eingetragen werden!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else if (event.getStartDate().before(getStartDateToday()) || event.getEndDate().before(getStartDateToday())
                || event.getEndDate().after(getEndDateToday()) || event.getStartDate().after(getEndDateToday())) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Die eingebene Zeit ist nicht erlaubt!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else if (!workTimes.isEmpty() && event.getId() == null) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Nur eine Arbeitszeit pro Tag!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else if (!checkAvailableTime(event.getStartDate(), event.getEndDate(), false)) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Ein Eintrag für diese Urzeit ist bereits vorhanden!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else {
            if (event.getId() == null) {
                wt = new WorkTime(currentUser, startDT, endDT, breaktime, startcomment, endcomment);

                IstZeitService.addIstTime(wt);

            } else if (event instanceof WorkTimeEvent) {
                wt = ((WorkTimeEvent) event).getWorktime();
                LocalTime plus19 = LocalTime.of(19, 0);
                diff -= wt.getStartTime().toLocalTime().until(wt.getSollStartTime(), ChronoUnit.MINUTES);
                if (wt.getEndTime().toLocalTime().isAfter(plus19)) {
                    diff -= wt.getOvertimeAfter19() * 1.5;
                    diff -= wt.getSollEndTime().until(plus19, ChronoUnit.MINUTES);
                } else {
                    diff -= wt.getSollEndTime().until(wt.getEndTime().toLocalTime(), ChronoUnit.MINUTES);
                }

                wt.setBreakTime(breaktime);
                wt.setStartComment(startcomment);
                wt.setEndComment(endcomment);
                wt.setStartTime(startDT);
                wt.setEndTime(endDT);

                IstZeitService.update(wt);
            } else if (event instanceof AbsenceEvent) {

//                AbsenceService.updateAbsence(((AbsenceEvent) event).getAbsence());
            }

            if (wt != null) {

                LocalTime plus19 = LocalTime.of(19, 0);
                diff += wt.getStartTime().toLocalTime().until(wt.getSollStartTime(), ChronoUnit.MINUTES);
                if (wt.getEndTime().toLocalTime().isAfter(plus19)) {
                    diff += wt.getOvertimeAfter19() * 1.5;
                    diff += wt.getSollEndTime().until(plus19, ChronoUnit.MINUTES);
                } else {
                    diff += wt.getSollEndTime().until(wt.getEndTime().toLocalTime(), ChronoUnit.MINUTES);
                }
                wt.getUser().setOverTimeLeft(wt.getUser().getOverTimeLeft() + diff);
                BenutzerverwaltungService.updateUser(wt.getUser());
            }
            event = new DefaultScheduleEvent();
        }
    }

    public boolean checkAvailableTime(Date startD, Date endD, boolean checkWorkTime) {
        boolean retVal = true;

        if (event instanceof AbsenceEvent && type.getAbsenceName().contentEquals("business-related absence")) {
        } else {
            LocalDateTime startDT = TimeConverterService.convertDateToLocalDateTime(startD);
            LocalDateTime endDT = TimeConverterService.convertDateToLocalDateTime(endD);

            List<Holiday> holList = HolidayService.getHolidayBetweenDates(TimeConverterService.convertLocalDateTimeToDate(startDT.with(LocalTime.MIN)), TimeConverterService.convertLocalDateTimeToDate(endDT.plusDays(1).with(LocalTime.MIN)));

            if (checkWorkTime) {
                List<WorkTime> wtList = IstZeitService.getWorkTimeForUserBetweenStartAndEndDate(currentUser, TimeConverterService.convertLocalDateTimeToDate(startDT.with(LocalTime.MIN)), TimeConverterService.convertLocalDateTimeToDate(endDT.plusDays(1).with(LocalTime.MIN)));

                for (WorkTime wt : wtList) {
                    Date startWT = TimeConverterService.convertLocalDateTimeToDate(wt.getStartTime());
                    Date endWT = TimeConverterService.convertLocalDateTimeToDate(wt.getEndTime());
                    if (startD.after(startWT) && startD.before(endWT) || endD.after(startWT) && endD.before(endWT) || startD.before(startWT) && endD.after(endWT)) {
                        retVal = false;
                    }
                }
            }
            List<Absence> absList = AbsenceService.getAbsencesByUserBetweenDates(currentUser, startD, endD);

            for (Absence a : absList) {
                if (!a.getAbsenceType().getAbsenceName().contentEquals("business-related absence")) {
                    if (!startD.equals(TimeConverterService.convertLocalDateTimeToDate(a.getEndTime()))) {
                        if (event instanceof AbsenceEvent) {
                            if (((AbsenceEvent) event).getAbsence() != null && !((AbsenceEvent) event).getAbsence().equals(a)) {
                                retVal = false;
                            }
                        } else {
                            retVal = false;
                        }
                    }
                }

            }

            if (!holList.isEmpty()) {
                retVal = false;
            }
        }
        return retVal;

    }

    public void deleteIstZeitEvent(ActionEvent e) {
        if (event.getId() != null) {
            if (event instanceof WorkTimeEvent) {
                istZeitEventModel.deleteEvent(event);
                WorkTimeEvent workevent = (WorkTimeEvent) event;
                IstZeitService.delete(workevent.getWorktime());

                int diff = 0;
                WorkTime wt = workevent.getWorktime();
                LocalTime plus19 = LocalTime.of(19, 0);
                diff -= wt.getStartTime().toLocalTime().until(wt.getSollStartTime(), ChronoUnit.MINUTES);
                if (wt.getEndTime().toLocalTime().isAfter(plus19)) {
                    diff -= wt.getOvertimeAfter19() * 1.5;
                    diff -= wt.getSollEndTime().until(plus19, ChronoUnit.MINUTES);
                } else {
                    diff -= wt.getSollEndTime().until(wt.getEndTime().toLocalTime(), ChronoUnit.MINUTES);
                }
                wt.getUser().setOverTimeLeft(wt.getUser().getOverTimeLeft() + diff);
                BenutzerverwaltungService.updateUser(wt.getUser());

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
        Date sDate = (Date) selectEvent.getObject();
        if (sDate.before(getStartDateToday()) || sDate.after(getEndDateToday())) {
            FacesContext.getCurrentInstance().validationFailed();
        } else {

            LocalDate start = TimeConverterService.convertDateToLocalDate(sDate);

            SollZeit soll = SollZeitenService.getSollZeitByUserAndDayOfWeek(currentUser, start.getDayOfWeek());

            if (soll != null) {
                LocalDateTime s = start.atTime(soll.getSollStartTime());
                LocalDateTime e = start.atTime(soll.getSollEndTime());

                event = new WorkTimeEvent("", TimeConverterService.convertLocalDateTimeToDate(s), TimeConverterService.convertLocalDateTimeToDate(e), null);
            } else {
                event = new WorkTimeEvent("", sDate, sDate, null);
            }
        }
    }

    public void onAbsenceDateSelect(SelectEvent selectEvent) {
        Date sDate = (Date) selectEvent.getObject();
        event = new AbsenceEvent("", sDate, sDate, new Absence(this.currentUser, type, null, null));
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
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "You can't acknowledge your own abscences!"));

        } else {

            acknowledgementModel.deleteEvent(absenceEvent);

            User u = absenceEvent.getAbsence().getUser();

            absenceEvent.getAbsence().setAcknowledged(true);
            AbsenceService.updateAbsence(absenceEvent.getAbsence());
            if (absenceEvent.getAbsence().getAbsenceType().getAbsenceName().contentEquals("holiday")) {
                setHolidayAndIstZeiten(absenceEvent.getAbsence());
            } else {
                int overtime = calcAbsenceOvertime(absenceEvent.getAbsence());
                u.setOverTimeLeft(u.getOverTimeLeft() + overtime);
                BenutzerverwaltungService.updateUser(u);
            }

            List<User> otherApprover = absenceEvent.getAbsence().getUser().getApprover();
            otherApprover.remove(currentUser);
            EmailService.sendAcknowledgmentEmail(absenceEvent.getAbsence(), currentUser, otherApprover);

        }

        this.reloadAcknowledgements();

        absenceEvent = new AbsenceEvent();
    }

    public void deleteAcknowledgement() {
        if (absenceEvent.getId() == null) {

        } else {
            acknowledgementModel.deleteEvent(absenceEvent);
            AbsenceService.deleteAbsence(absenceEvent.getAbsence());

            if (absenceEvent.getAbsence().isAcknowledged()) {
                if (absenceEvent.getAbsence().getAbsenceType().getAbsenceName().equals("holiday")) {
                    removeHolidayAndIstZeiten(absenceEvent.getAbsence());
                } else {
                    User u = absenceEvent.getAbsence().getUser();
                    absenceEvent.getAbsence().setAcknowledged(false);
                    int overtime = calcAbsenceOvertime(absenceEvent.getAbsence());

                    u.setOverTimeLeft(u.getOverTimeLeft() - overtime);
                    BenutzerverwaltungService.updateUser(u);
                }
            }

            List<User> otherApprover = absenceEvent.getAbsence().getUser().getApprover();
            otherApprover.remove(currentUser);
            EmailService.sendAbsenceDeletedByApprover(absenceEvent.getAbsence(), currentUser, otherApprover);

        }
    }

    public void deleteAcknowledgedAbsenceFromOverviews() {
        if (event.getId() == null) {

        } else {
            Absence a = ((AbsenceEvent) event).getAbsence();
            AbsenceService.deleteAbsence(a);

            if (a.isAcknowledged()) {
                if (a.getAbsenceType().getAbsenceName().equals("holiday")) {
                    removeHolidayAndIstZeiten(a);
                } else {

                    a.setAcknowledged(false);
                    User u = a.getUser();
                    int overtime = calcAbsenceOvertime(a);

                    u.setOverTimeLeft(u.getOverTimeLeft() - overtime);
                    BenutzerverwaltungService.updateUser(u);
                }
            }

            List<User> otherApprover = a.getUser().getApprover();
            otherApprover.remove(currentUser);
            EmailService.sendAbsenceDeletedByApprover(a, currentUser, otherApprover);

        }
    }
    
    //adds Worktimes and removes Vacation if Absence is aknowledged
    private void setHolidayAndIstZeiten(Absence a) {
        User u = a.getUser();
        List<SollZeit> sollZeiten = SollZeitenService.getSollZeitenByUser(u);
        int days = 0;
        if (a.getAbsenceType().getAbsenceName().contentEquals("holiday") && a.isAcknowledged()) {
            days = (int) (a.getStartTime().until(a.getEndTime(), ChronoUnit.DAYS) + 1);

            u.setVacationLeft(u.getVacationLeft() - days);
            BenutzerverwaltungService.updateUser(u);

            LocalDateTime day = a.getStartTime();
            for (int i = 0; i < days; i++, day = day.plusDays(1)) {
                for (SollZeit sz : sollZeiten) {
                    if (sz.getDay().equals(day.getDayOfWeek())) {
                        int breaktime = 0;
                        if (sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.HOURS) >= 6) {
                            breaktime = 30;
                        }
                        IstZeitService.addIstTime(new WorkTime(u, day.with(sz.getSollStartTime()), day.with(sz.getSollEndTime()), breaktime, "Urlaub", "Urlaub"));
                    }
                }
            }
        }
    }
    
    //removes Worktimes and adds Vacation if Absence is aknowledged
    private void removeHolidayAndIstZeiten(Absence a) {
        User u = a.getUser();
        int days = 0;
        if (a.getAbsenceType().getAbsenceName().contentEquals("holiday") && a.isAcknowledged()) {
            days = (int) (a.getStartTime().until(a.getEndTime(), ChronoUnit.DAYS) + 1);

            u.setVacationLeft(u.getVacationLeft() + days);
            BenutzerverwaltungService.updateUser(u);

            List<WorkTime> workTimes = IstZeitService.getWorkTimeForUserBetweenStartAndEndDate(u, TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime().toLocalDate().plusDays(1).atStartOfDay()));
            for (WorkTime wt : workTimes) {
                IstZeitService.delete(wt);
            }
        }
    }

// Wenn eine angenommene Abwesenheit übergeben wird, wird der Urlaub dazugerechnet sonst wird sie abgezogen 
    private int calcAbsenceOvertime(Absence a) {
        int overtime = 0;
        User u = a.getUser();
        List<SollZeit> sollZeiten = SollZeitenService.getSollZeitenByUser(u);
        int days = 0;
        DayOfWeek sDay;

        switch (a.getAbsenceType().getAbsenceName()) {
            case "holiday":
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
        if (type.getAbsenceName().equals("holiday")) {
            return "dd.MM.yyyy";
        } else {
            return "dd.MM.yyyy HH:mm";
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
        return currentUser.getOverTimeLeft();
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

        this.istZeitEventModel.deleteEvent(absenceevent);
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

    @Override
    public void notifyObserver() {
        currentUser = BenutzerverwaltungService.getUser(currentUser.getUserNr());
        ((IstZeitLazyScheduleModel) istZeitEventModel).setCurrentUser(currentUser);
    }
}
