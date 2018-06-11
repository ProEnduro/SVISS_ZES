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
import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.dao.util.DAODML_Observer;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.AbsenceTypeNew;
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
import edu.emory.mathcs.backport.java.util.Arrays;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
//import org.glassfish.hk2.utilities.reflection.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 * Bean which is used by "allTime.xhtml", "istzeit.xhtml", "abwesenheit.xhtml",
 * "view_all_abwesenheiten.xhtml", "absence_acknowledgments.xhtml"
 *
 * @author msi
 */
public class ScheduleView implements Serializable, DAODML_Observer {

    private static final long serialVersionUID = 1L;

    private AbsenceTypeNew type;
    private User currentUser;

    private ScheduleModel istZeitEventModel;

    private AcknowledgeLazyScheduleModel acknowledgementModel;
    private AllTimeLazyScheduleModel allTimesModel;
    private AlleAbwesenheitenLazyScheduleModel alleAbwesenheitenModel;

    private ScheduleEvent event = new DefaultScheduleEvent();

    private AbsenceEvent absenceEvent = new AbsenceEvent();
    private WorkTimeEvent worktimeEvent = new WorkTimeEvent();

    private String selectedUser;
    private List<String> allUsers;

    private double verbleibend;
    private double overtimeleft;

    private String startcomment;
    private String endcomment;
    private String reason;

    private int breaktime;
    private boolean updateMode;
    public boolean dateChanged;

    Integer breaktimeOld;

    boolean isAdminInputingOtherPeoplesAbsences = false;
    String userWhoseAbsenceTheAdminInputsInStringFormat = null;
    boolean oldWorktime;

    /**
     * Initializes various models for the multiple pages
     */
    @PostConstruct
    public void init() {
        updateMode = false;
        dateChanged = false;
        breaktimeOld = null;
        isAdminInputingOtherPeoplesAbsences = false;
        oldWorktime = false;

        FacesContext context = FacesContext.getCurrentInstance();
        MasterBean masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);
        currentUser = masterBean.getUser();
        if (currentUser != null) {
            BenutzerverwaltungService.addUserObserver(this);

            type = AbsenceTypeNew.MEDICAL_LEAVE;

            istZeitEventModel = new IstZeitLazyScheduleModel(currentUser);
            acknowledgementModel = new AcknowledgeLazyScheduleModel();
            allTimesModel = new AllTimeLazyScheduleModel();
            alleAbwesenheitenModel = new AlleAbwesenheitenLazyScheduleModel();

            this.reloadAbwesenheiten();
        }

        this.onLoad(null);
    }

    @PreDestroy
    private void destroy() {
        if (currentUser != null) {
            BenutzerverwaltungService.deleteUserObserver(this);
        }
    }

    /**
     * calculates the default Breaktime according to startDate and endDate of
     * event
     *
     * @return Breaktime in Minutes
     */
    public int getBreaktime() {
        if (event instanceof WorkTimeEvent) {
            // Logger.getLogger(ScheduleView.class.getName()).log(Level.INFO, "This is a WorkTimeEvent!");
        } else {
            // Logger.getLogger(ScheduleView.class.getName()).log(Level.INFO, "This is NOT a WorkTimeEvent but it could be null!");
            if (event.getStartDate() != null && event.getEndDate() != null) {
                // Logger.getLogger(ScheduleView.class.getName()).log(Level.INFO, "This is NOT a WorkTimeEvent and it is not null!");
                if (IstZeitService.breakTimeBooleanCalc(event.getStartDate(), event.getEndDate())) {
                    return 30;
                } else {
                    return 0;
                }
            }
        }

        // Logger.getLogger(ScheduleView.class.getName()).log(Level.INFO, this.newTimeSelected + "");
        RequestContext.getCurrentInstance().execute("PrimeFaces.info('dateChanged (getBreaktime): " + this.dateChanged + "');");
        if (this.newTimeSelected) {
            if (event instanceof WorkTimeEvent) {
                WorkTimeEvent wte = (WorkTimeEvent) event;
                if (wte != null && wte.getWorktime() != null && wte.getWorktime().getBreakTime() != null) {
                    if (wte.getWorktime().getBreakTime() != 0 || wte.getWorktime().getBreakTime() != 30) {
                        RequestContext.getCurrentInstance().execute("PrimeFaces.info('Breaktime X: " + breaktime + "');");
                        RequestContext.getCurrentInstance().execute("PrimeFaces.info('Breaktime X WTE: " + wte.getWorktime().getBreakTime() + "');");
                        return wte.getWorktime().getBreakTime();
                    }
                }
            }

            if (IstZeitService.breakTimeBooleanCalc(event.getStartDate(), event.getEndDate())) {
                RequestContext.getCurrentInstance().execute("PrimeFaces.info('Breaktime 30: " + 30 + "');");
                return 30;
            } else {
                RequestContext.getCurrentInstance().execute("PrimeFaces.info('Breaktime 0: " + 0 + "');");
                return 0;
            }
        } else {
            if (dateChanged) {
                if (IstZeitService.breakTimeBooleanCalc(event.getStartDate(), event.getEndDate())) {
                    RequestContext.getCurrentInstance().execute("PrimeFaces.info('Breaktime 30: " + 30 + "');");
                    return 30;
                } else {
                    RequestContext.getCurrentInstance().execute("PrimeFaces.info('Breaktime 0: " + 0 + "');");
                    return 0;
                }
            } else {
                RequestContext.getCurrentInstance().execute("PrimeFaces.info('Breaktime X: " + breaktime + "');");
                return this.breaktime;
            }
        }
//        RequestContext.getCurrentInstance().execute("PrimeFaces.info('Breaktime X: " + breaktime + "');");
//        this.newTimeSelected = false;
//        return breaktime;
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

    public void setIsAdminInputingOtherPeoplesAbsencesVariable(ActionEvent actionEvent) {
        if (userWhoseAbsenceTheAdminInputsInStringFormat != null) {
            if (!userWhoseAbsenceTheAdminInputsInStringFormat.equals("-")) {
                this.isAdminInputingOtherPeoplesAbsences = true;
                User u = BenutzerverwaltungService.getUser(userWhoseAbsenceTheAdminInputsInStringFormat);
                FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, "INFO", "Sie können nun Abwesenheiten für den Benutzer " + u.getUsername() + ": " + u.getPersName() + " eintragen!"));
            } else {
                this.isAdminInputingOtherPeoplesAbsences = false;
            }
        } else {
            this.isAdminInputingOtherPeoplesAbsences = false;
        }
    }

    /**
     * adds a new Absence or updates one if validation succeeds, used in
     * "absence.xhtml"
     *
     * @param actionEvent ActionEvent
     */
    public void addAbsenceEvent(ActionEvent actionEvent) {
        if (isAdminInputingOtherPeoplesAbsences == false) {

            LocalDateTime startDT = TimeConverterService.convertDateToLocalDateTime(event.getStartDate());
            LocalDateTime endDT = TimeConverterService.convertDateToLocalDateTime(event.getEndDate());

            if ((!startDT.toLocalDate().equals(endDT.toLocalDate()) && endDT.toLocalTime().equals(LocalTime.MIN)) || startDT.equals(endDT)) {
                startDT = startDT.with(LocalTime.MIN);
                endDT = endDT.with(LocalTime.of(23, 59, 59));
            }
            //Validation
            if (event.getStartDate().after(event.getEndDate())) {
                FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Endzeitpunkt ist vor Startzeitpunkt!"));
                FacesContext.getCurrentInstance().validationFailed();
            } else if (!checkAvailableTime(TimeConverterService.convertLocalDateTimeToDate(startDT), TimeConverterService.convertLocalDateTimeToDate(endDT), true)) {
                FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Ein Eintrag für diese Uhrzeit ist bereits vorhanden!"));
                FacesContext.getCurrentInstance().validationFailed();
            } else if (!FacesContext.getCurrentInstance().isValidationFailed() && type.equals(AbsenceTypeNew.HOLIDAY)) {
                int days = 0;
                SollZeit currentSollZeit = SollZeitenService.getSollZeitenByUser_Current(currentUser);
                if (currentSollZeit != null) {
                    for (LocalDate ld = startDT.toLocalDate(); ld.atStartOfDay().isBefore(endDT); ld = ld.plusDays(1)) {

                        if (currentSollZeit.getSollStartTimeMap().containsKey(ld.getDayOfWeek())) {
                            days++;
                        }

                    }
                }
                if (days > currentUser.getVacationLeft()) {
                    FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Sie haben nicht genug Urlaub übrig!"));
                    FacesContext.getCurrentInstance().validationFailed();
                }
            }
            //Inserts or updates

            if (!FacesContext.getCurrentInstance().isValidationFailed() && event instanceof AbsenceEvent && event.getId() == null) {

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
//            IstZeitService.update(((WorkTimeEvent) event).getWorktime());
            } else if (event instanceof AbsenceEvent) {
                Absence a = ((AbsenceEvent) event).getAbsence();
                a.setReason(reason);
                a.setStartTime(startDT);
                a.setEndTime(endDT);
                AbsenceService.updateAbsence(a);
            }

            event = new DefaultScheduleEvent();
        } else {

            User userWhoseAbsenceTheAdminInputs = BenutzerverwaltungService.getUser(userWhoseAbsenceTheAdminInputsInStringFormat);

            LocalDateTime startDT = TimeConverterService.convertDateToLocalDateTime(event.getStartDate());
            LocalDateTime endDT = TimeConverterService.convertDateToLocalDateTime(event.getEndDate());

            if ((!startDT.toLocalDate().equals(endDT.toLocalDate()) && endDT.toLocalTime().equals(LocalTime.MIN)) || startDT.equals(endDT)) {
                startDT = startDT.with(LocalTime.MIN);
                endDT = endDT.with(LocalTime.of(23, 59, 59));
            }
            //Validation
            if (event.getStartDate().after(event.getEndDate())) {
                FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Endzeitpunkt ist vor Startzeitpunkt!"));
                FacesContext.getCurrentInstance().validationFailed();
            } else {
                Absence a = ((AbsenceEvent) event).getAbsence();
                a.setAbsenceType(type);
                // FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, "INFO", a.getAbsenceType().getMyString()));
                if (!a.getAbsenceType().equals(AbsenceTypeNew.MEDICAL_LEAVE)) {
                    FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Derzeit können nur Krankenstände vom Admin eingetragen werden!"));
                    FacesContext.getCurrentInstance().validationFailed();
                }
            }
            if (!FacesContext.getCurrentInstance().isValidationFailed() && event instanceof AbsenceEvent && event.getId() == null) {

                FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_INFO, "INFO", "Passed: " + ((AbsenceEvent) event).getAbsence().getAbsenceType().getMyString()));

                Absence a = ((AbsenceEvent) event).getAbsence();
                a.setStartTime(startDT);
                a.setEndTime(endDT);
                a.setReason(this.reason);
                a.setUser(userWhoseAbsenceTheAdminInputs);

                AbsenceService.insertAbsence(a);
                List<User> approverlist = currentUser.getApprover();
                if (!approverlist.isEmpty()) {
                    EmailService.sendUserEnteredAbsenceEmail(a, approverlist);
                }
            } else if (event instanceof WorkTimeEvent) {
//            IstZeitService.update(((WorkTimeEvent) event).getWorktime());
            } else if (event instanceof AbsenceEvent) {
                Absence a = ((AbsenceEvent) event).getAbsence();
                a.setReason(reason);
                a.setStartTime(startDT);
                a.setEndTime(endDT);
                a.setUser(userWhoseAbsenceTheAdminInputs);
                AbsenceService.updateAbsence(a);
            }

            event = new DefaultScheduleEvent();
        }
    }

    /**
     * Checks if the current User is allowed to the delete an Absence, used in
     * "absence.xhtml"
     *
     * @return is the currentUser able to?
     */
    public boolean isAbleToDeleteAbsence() {
        return currentUser != null && AccessRightsService.checkPermission(currentUser.getAccessLevel(), "ALL");
    }

    /**
     * deletes the Absence in event if validation succeeds, used in
     * "absence.xhtml"
     */
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

    /**
     * Method updates or adds a Worktime if the Validation succeeds used in
     * "istzeit.xhtml"
     *
     * @param actionEvent ActionEvent
     */
    public void addIstZeitEvent(ActionEvent actionEvent) {
        int diff = 0;

        LocalDateTime startDT = TimeConverterService.convertDateToLocalDateTime(event.getStartDate());
        LocalDateTime endDT = TimeConverterService.convertDateToLocalDateTime(event.getEndDate());

        WorkTime wt = null;

        breaktimeOld = null;

        //List<WorkTime> workTimes = IstZeitService.getWorkTimeForUserBetweenStartAndEndDate(currentUser, TimeConverterService.convertLocalDateTimeToDate(startDT.with(LocalTime.MIN)), TimeConverterService.convertLocalDateTimeToDate(startDT.with(LocalTime.MIN).plusDays(1)));
        if (event.getStartDate().after(event.getEndDate())) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Endzeitpunkt ist vor Startzeitpunkt!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else if (!startDT.toLocalDate().equals(endDT.toLocalDate())) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Arbeitszeit kann nur an einem Tag eingetragen werden!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else if (!AccessRightsService.checkPermission(currentUser.getAccessLevel(), "ALL") && (event.getStartDate().before(getAllowedStartDateToday()) || event.getEndDate().before(getAllowedStartDateToday())
                || event.getEndDate().after(getAllowedEndDateToday()) || event.getStartDate().after(getAllowedEndDateToday()))) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Die eingebene Zeit ist nicht erlaubt!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else if (!checkAvailableTime(event.getStartDate(), event.getEndDate(), false)) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Ein Eintrag für diese Uhrzeit ist bereits vorhanden!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else if (duplicateIstZeitEntries(event.getStartDate(), event.getEndDate())) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Mehrere Ist-Zeiten an einem Tag dürfen sich nicht überschneiden!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else {
            if (!FacesContext.getCurrentInstance().isValidationFailed() && event.getId() == null) {
                wt = new WorkTime(currentUser, startDT, endDT, breaktime, startcomment, endcomment);

                IstZeitService.addIstTime(wt);

            } else if (event instanceof WorkTimeEvent) {
                wt = ((WorkTimeEvent) event).getWorktime();
                LocalTime plus19 = LocalTime.of(19, 0);
                if (wt.getSollStartTime() != null) {
                    diff -= wt.getStartTime().toLocalTime().until(wt.getSollStartTime(), ChronoUnit.MINUTES);
                    if (wt.getEndTime().toLocalTime().isAfter(plus19)) {
                        diff -= wt.getOvertimeAfter19() * 1.5;
                        diff -= wt.getSollEndTime().until(plus19, ChronoUnit.MINUTES);
                    } else {
                        diff -= wt.getSollEndTime().until(wt.getEndTime().toLocalTime(), ChronoUnit.MINUTES);
                    }
                } else {
                    //detract all time (would be overtime)
                    if (wt.getEndTime().toLocalTime().isAfter(plus19)) {
                        diff -= wt.getOvertimeAfter19() * 1.5;
                        diff -= wt.getStartTime().toLocalTime().until(plus19, ChronoUnit.MINUTES);
                    } else {
                        diff -= wt.getStartTime().until(wt.getEndTime(), ChronoUnit.MINUTES);
                    }
                }

                // Logger.getLogger(ScheduleView.class.getName()).log(Level.INFO, wt.getBreakTime() + "");
                breaktimeOld = wt.getBreakTime();

                wt.setBreakTime(breaktime);
                wt.setStartComment(startcomment);
                wt.setEndComment(endcomment);
                wt.setStartTime(startDT);
                wt.setEndTime(endDT);

                IstZeitService.update(wt);
                updateMode = true;
            } //else if (event instanceof AbsenceEvent) {

//                AbsenceService.updateAbsence(((AbsenceEvent) event).getAbsence());
            // }
            if (wt != null) {

                LocalTime plus19 = LocalTime.of(19, 0);
                if (wt.getSollStartTime() != null) {
                    diff += wt.getStartTime().toLocalTime().until(wt.getSollStartTime(), ChronoUnit.MINUTES);
                    if (wt.getEndTime().toLocalTime().isAfter(plus19)) {
                        diff += wt.getOvertimeAfter19() * 1.5;
                        diff += wt.getSollEndTime().until(plus19, ChronoUnit.MINUTES);
                    } else {
                        diff += wt.getSollEndTime().until(wt.getEndTime().toLocalTime(), ChronoUnit.MINUTES);
                    }
                } else {
                    //detract all time (would be overtime)
                    if (wt.getEndTime().toLocalTime().isAfter(plus19)) {
                        diff += wt.getOvertimeAfter19() * 1.5;
                        diff += wt.getStartTime().toLocalTime().until(plus19, ChronoUnit.MINUTES);
                    } else {
                        diff += wt.getStartTime().until(wt.getEndTime(), ChronoUnit.MINUTES);
                    }
                }

//                if(wt.getStartTime().until(wt.getEndTime(), ChronoUnit.HOURS) >= 6){
//                    diff-= wt.getBreakTime();
//                }
                if (wt.getStartTime() != null && (wt.getStartTime().until(wt.getEndTime(), ChronoUnit.HOURS) > 6)) {
                    if (wt.getSollStartTime().until(wt.getSollEndTime(), ChronoUnit.HOURS) > 6) {
                        diff += 30;
                    }
//                    diff += 30;
                }

                diff -= wt.getBreakTime();

                if (breaktimeOld != null) {
                    if (wt.getBreakTime() < breaktimeOld) {
                        diff = diff + (breaktimeOld - wt.getBreakTime());
                    }
                }

                if (!updateMode) {
                    LocalDate now = LocalDate.now();
                    if (wt.getStartTime().getDayOfMonth() < now.getDayOfMonth()) {
                        diff = (int) wt.getStartTime().until(wt.getEndTime(), ChronoUnit.MINUTES);
                        diff = diff - wt.getBreakTime();
                    }
                }

                wt.getUser().setOverTimeLeft(wt.getUser().getOverTimeLeft() + diff);
                BenutzerverwaltungService.updateUser(wt.getUser());
            }
            event = new DefaultScheduleEvent();
            updateMode = false;
            oldWorktime = false;
        }
    }

    /**
     * checks if a WorkTime or an Absence may be inserted in this timearea
     *
     * @param startD startDate for the timearea
     * @param endD endDate for the timearea
     * @param checkWorkTime boolean whether it should check for WorkTime(true)
     * or not(false
     * @return boolean whether inserting or updating is allowed
     */
    public boolean checkAvailableTime(Date startD, Date endD, boolean checkWorkTime) {
        boolean retVal = true;

        if (event instanceof AbsenceEvent && !type.equals(AbsenceTypeNew.BUSINESSRELATED_ABSENCE)) {
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
                if (!a.getAbsenceType().equals(AbsenceTypeNew.BUSINESSRELATED_ABSENCE)) {
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

    public boolean duplicateIstZeitEntries(Date start, Date end) {
        boolean retVal = false;

        if (oldWorktime == false) {

            LocalDateTime startDT = TimeConverterService.convertDateToLocalDateTime(start);
            LocalDateTime endDT = TimeConverterService.convertDateToLocalDateTime(end);

            List<WorkTime> worklist = IstZeitService.getWorkTimeForUserBetweenStartAndEndDate(currentUser, TimeConverterService.convertLocalDateTimeToDate(startDT.with(LocalTime.MIN)), TimeConverterService.convertLocalDateTimeToDate(endDT.plusDays(1).with(LocalTime.MIN)));

            for (WorkTime wt : worklist) {
                Date startWT = TimeConverterService.convertLocalDateTimeToDate(wt.getStartTime());
                Date endWT = TimeConverterService.convertLocalDateTimeToDate(wt.getEndTime());
                if (start.after(startWT) && start.before(endWT) || end.after(startWT) && end.before(endWT) || start.before(startWT) && end.after(endWT) || end.equals(endWT) || start.equals(startWT)) {
                    retVal = true;
                }
            }
        }

        return retVal;
    }

    /**
     * deletes a WorkTime from event, used in "istzeit.xhtml"
     *
     * @param e ActionEvent
     */
    public void deleteIstZeitEvent(ActionEvent e) {
        if (event.getId() != null) {
            if (event instanceof WorkTimeEvent) {
                istZeitEventModel.deleteEvent(event);
                WorkTimeEvent workevent = (WorkTimeEvent) event;
                IstZeitService.delete(workevent.getWorktime());

                int diff = 0;
                WorkTime wt = workevent.getWorktime();
                LocalTime plus19 = LocalTime.of(19, 0);
//                if (wt.getSollStartTime() != null) {
//                    diff -= wt.getStartTime().toLocalTime().until(wt.getSollStartTime(), ChronoUnit.MINUTES);
//                    if (wt.getEndTime().toLocalTime().isAfter(plus19)) {
//                        diff -= wt.getOvertimeAfter19() * 1.5;
//                        diff -= wt.getSollEndTime().until(plus19, ChronoUnit.MINUTES);
//                    } else {
//                        diff -= wt.getSollEndTime().until(wt.getEndTime().toLocalTime(), ChronoUnit.MINUTES);
//                    }
//                } else {
//                    //detract all time (would be overtime)
//                    if (wt.getEndTime().toLocalTime().isAfter(plus19)) {
//                        diff -= wt.getOvertimeAfter19() * 1.5;
//                        diff -= wt.getStartTime().toLocalTime().until(plus19, ChronoUnit.MINUTES);
//                    } else {
//                        diff -= wt.getStartTime().until(wt.getEndTime(), ChronoUnit.MINUTES);
//                    }
//                }

                //Test
                LocalDate now = LocalDate.now();
                SollZeiten_DAO SOLL_ZEITEN_DAO = DAOFactory.getDAOFactory().getSollZeitenDAO();
                SollZeit sz = SOLL_ZEITEN_DAO.getSollZeitenByUser_Current(wt.getUser());

                if (wt.getStartTime().getDayOfMonth() < now.getDayOfMonth() || sz == null) {
                    if (wt.getEndTime().toLocalTime().isAfter(plus19)) {
                        diff -= wt.getOvertimeAfter19() * 1.5;
                        diff -= wt.getStartTime().toLocalTime().until(plus19, ChronoUnit.MINUTES);
                    } else {
                        diff -= wt.getStartTime().until(wt.getEndTime(), ChronoUnit.MINUTES);
                    }
                    diff += wt.getBreakTime();
                } else {
                    int realworktime = (int) (wt.getStartTime().until(wt.getEndTime(), ChronoUnit.MINUTES) - wt.getBreakTime());
                    int realsolltime = (int) wt.getSollStartTime().until(wt.getSollEndTime(), ChronoUnit.MINUTES);
                    if (realsolltime >= 360) {
                        realsolltime = realsolltime - 30;
                    }
                    diff = realsolltime - realworktime;
                }

                wt.getUser().setOverTimeLeft(wt.getUser().getOverTimeLeft() + diff);
                BenutzerverwaltungService.updateUser(wt.getUser());

                event = new DefaultScheduleEvent();
            }
        }
    }

    /**
     * sets the selected event as data for the WorkTime-Dialog and opens it,
     * used in "istzeit.xhtml"
     *
     * @param selectEvent SelectEvent
     */
    public void onIstZeitEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();

        startcomment = "";
        endcomment = "";
        breaktime = 0;
        reason = "";

        if (event instanceof WorkTimeEvent) {
            WorkTimeEvent ev = (WorkTimeEvent) event;
            startcomment = ev.getWorktime().getStartComment();
            endcomment = ev.getWorktime().getEndComment();
            breaktime = ev.getWorktime().getBreakTime();

            oldWorktime = true;

            RequestContext.getCurrentInstance().execute("PF('eventDialog').show();");

        }
    }

    /**
     * Handles Event-Clicks, sets the data for Dialogs and opens them, used in
     * "allTime.xhtml" and "abwesenheiten.xhtml"
     *
     * @param selectEvent SelectEvent
     */
    public void onEventInAbwesenheitenSelect(SelectEvent selectEvent) {
        isAdminInputingOtherPeoplesAbsences = false;
        this.onLoad(null);
        this.userWhoseAbsenceTheAdminInputsInStringFormat = null;

        event = (ScheduleEvent) selectEvent.getObject();

        startcomment = "";
        endcomment = "";
        breaktime = 0;
        reason = "";

        if (event instanceof WorkTimeEvent) {
            WorkTimeEvent ev = (WorkTimeEvent) event;
            startcomment = ev.getWorktime().getStartComment();
            endcomment = ev.getWorktime().getEndComment();
            breaktime = ev.getWorktime().getBreakTime();

            RequestContext.getCurrentInstance().execute("PF('worktimeDialog').show();");

        } else if (event instanceof AbsenceEvent) {
            AbsenceEvent ev = (AbsenceEvent) event;
            reason = ev.getAbsence().getReason();
            RequestContext.getCurrentInstance().execute("PF('absenceDialog').show();");
        } else {
            RequestContext.getCurrentInstance().execute(("PF('feiertagDialog').show();"));
        }
    }

    /**
     * Sets the event used in the WorkTime-Creation-Dialog for creating a new
     * WorkTime, used in "istzeit.xhtml"
     *
     * @param selectEvent SelectEvent
     */
    boolean newTimeSelected = false;

    public void onIstZeitDateSelect(SelectEvent selectEvent) {
        oldWorktime = false;
        Date sDate = (Date) selectEvent.getObject();
        if (!AccessRightsService.checkPermission(currentUser.getAccessLevel(), "ALL") && (sDate.before(getAllowedStartDateToday()) || sDate.after(getAllowedEndDateToday()))) {
            FacesContext.getCurrentInstance().validationFailed();
        } else {

            LocalDate startDate = TimeConverterService.convertDateToLocalDate(sDate);

            SollZeit soll = SollZeitenService.getSollZeitenByUser_Current(currentUser);

            startcomment = "";
            endcomment = "";

            DayOfWeek dow = startDate.getDayOfWeek();
            if (soll != null && soll.getSollStartTime(dow) != null) {

                LocalDateTime sdt = startDate.atTime(soll.getSollStartTime(dow));
                LocalDateTime edt = startDate.atTime(soll.getSollEndTime(dow));

                event = new WorkTimeEvent("", TimeConverterService.convertLocalDateTimeToDate(sdt), TimeConverterService.convertLocalDateTimeToDate(edt), null);

                if (sdt.until(edt, ChronoUnit.HOURS) >= 6) {
                    this.breaktime = 30;
                } else {
                    this.breaktime = 0;
                }
            } else {
                event = new WorkTimeEvent("", sDate, sDate, null);
            }
            this.newTimeSelected = true;
        }
    }

    /**
     * sets the event to be displayed when an Absence is selected, used in
     * "abwesenheit.xhtml"
     *
     * @param selectEvent SelectEvent
     */
    public void onAbsenceDateSelect(SelectEvent selectEvent) {
        isAdminInputingOtherPeoplesAbsences = false;
        Date sDate = (Date) selectEvent.getObject();
        event = new AbsenceEvent("", sDate, sDate, new Absence(this.currentUser, type, null, null));
    }

    public void onAbsenceOfOtherPeopleSelect(SelectEvent selectEvent) {
        if (this.isAdminInputingOtherPeoplesAbsences) {
            User userWhoseAbsenceTheAdminInputs = BenutzerverwaltungService.getUser(this.userWhoseAbsenceTheAdminInputsInStringFormat);
            Date sDate = (Date) selectEvent.getObject();
            event = new AbsenceEvent("", sDate, sDate, new Absence(userWhoseAbsenceTheAdminInputs, type, null, null));
            RequestContext.getCurrentInstance().execute("PF('eventDialog').show();");
        }
    }

    public AbsenceTypeNew getType() {
        return type;
    }

    public void setType(AbsenceTypeNew type) {
        this.type = type;
    }

    public List<AbsenceTypeNew> getTypes() {
        return new LinkedList(Arrays.asList(AbsenceTypeNew.values()));
    }

    /**
     * Set the currently selected Absence as acknowledged, if validation
     * succeeds, used in "absence_acknowledgement.xhtml"
     *
     * @param actionEvent ActionEvent
     */
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
            if (absenceEvent.getAbsence().getAbsenceType().equals(AbsenceTypeNew.HOLIDAY)) {
                setHolidayAndIstZeiten(absenceEvent.getAbsence());
            } else if (absenceEvent.getAbsence().getAbsenceType().equals(AbsenceTypeNew.TIME_COMPENSATION)) {
                // Do nothing -> timecompensation will be deducted after the day finishes by the OvertimeSynchronisationTask
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

    /**
     * deletes an Absence regardless of Acknowledgment and recalculates the
     * overtime accordingly and holiday of the user, used in
     * "absence_acknowledgement.xhtml"
     */
    public void deleteAcknowledgement() {
        if (absenceEvent.getId() == null) {

        } else {
            acknowledgementModel.deleteEvent(absenceEvent);
            AbsenceService.deleteAbsence(absenceEvent.getAbsence());

            if (absenceEvent.getAbsence().isAcknowledged()) {
                if (absenceEvent.getAbsence().getAbsenceType().equals(AbsenceTypeNew.HOLIDAY)) {
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

    /**
     * deletes an Absence from the DAOs and sends according emails, used in
     * "allTime.xhtml" and "view_all_abwesenheiten.xhtml"
     */
    public void deleteAcknowledgedAbsenceFromOverviews() {
        if (event.getId() == null) {

        } else {
            Absence a = ((AbsenceEvent) event).getAbsence();
            AbsenceService.deleteAbsence(a);

            if (a.isAcknowledged()) {
                if (a.getAbsenceType().equals(AbsenceTypeNew.HOLIDAY)) {
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

    /**
     * adds according Worktimes and removes the Vacation from the User if the
     * {@link AbsenceTypeNew#HOLIDAY}-Absence is aknowledged and will be
     * inserted
     *
     * @param a Absence which will be inserted and is checked
     *
     */
    private void setHolidayAndIstZeiten(Absence a) {
        User u = BenutzerverwaltungService.getUser(a.getUser().getUserNr());
        SollZeit sollZeit = SollZeitenService.getSollZeitenByUser_Current(u);
        int days;
        if (a.getAbsenceType().equals(AbsenceTypeNew.HOLIDAY) && a.isAcknowledged()) {
            days = (int) (a.getStartTime().until(a.getEndTime(), ChronoUnit.DAYS) + 1);

            //Logger.getLogger(ScheduleView.class.getName()).log(Level.INFO, "Days: " + days);
            Logger.getLogger(ScheduleView.class.getName()).log(Level.INFO, "Before From Database: " + BenutzerverwaltungService.getUser(u.getUserNr()).getPersName() + " " + BenutzerverwaltungService.getUser(u.getUserNr()).getVacationLeft());
            Logger.getLogger(ScheduleView.class.getName()).log(Level.INFO, "Before: " + u.getVacationLeft());

            u.setVacationLeft(u.getVacationLeft() - days);
            Logger.getLogger(ScheduleView.class.getName()).log(Level.INFO, "After: " + u.getVacationLeft());
            BenutzerverwaltungService.updateUser(u);

            Logger.getLogger(ScheduleView.class.getName()).log(Level.INFO, "From Database: " + BenutzerverwaltungService.getUser(u.getUserNr()).getVacationLeft());

            LocalDateTime day = a.getStartTime();
            for (int i = 0; i < days; i++, day = day.plusDays(1)) {
                DayOfWeek dow = day.getDayOfWeek();

                if (sollZeit.getSollStartTimeMap().containsKey(dow)) {
                    int breaktime = 0;
                    if (sollZeit.getSollStartTime(dow).until(sollZeit.getSollEndTime(dow), ChronoUnit.HOURS) >= 6) {
                        breaktime = 30;
                    }
                    IstZeitService.addIstTime(new WorkTime(u, day.with(sollZeit.getSollStartTime(dow)), day.with(sollZeit.getSollEndTime(dow)), breaktime, "Urlaub", "Urlaub"));
                }
            }

        }
    }

    /**
     * removes Worktimes and readds Vacation to the User if the
     * {@link AbsenceTypeNew#HOLIDAY}-Absence is aknowledged and will be deleted
     *
     * @param a Absence which will be deleted and is checked
     */
    private void removeHolidayAndIstZeiten(Absence a) {
        User u = a.getUser();
        int days;
        if (a.getAbsenceType().equals(AbsenceTypeNew.HOLIDAY) && a.isAcknowledged()) {
            days = (int) (a.getStartTime().until(a.getEndTime(), ChronoUnit.DAYS) + 1);

            u.setVacationLeft(u.getVacationLeft() + days);
            BenutzerverwaltungService.updateUser(u);

            List<WorkTime> workTimes = IstZeitService.getWorkTimeForUserBetweenStartAndEndDate(u, TimeConverterService.convertLocalDateTimeToDate(a.getStartTime()), TimeConverterService.convertLocalDateTimeToDate(a.getEndTime().toLocalDate().plusDays(1).atStartOfDay()));
            workTimes.forEach((wt) -> {
                IstZeitService.delete(wt);
            });
        }
    }

    /**
     * Wenn eine angenommene Abwesenheit übergeben wird, werden die Überstunden
     * entsprechend berechnet und als Minuten zurückgegeben.
     *
     * @param a Absence which will be used for the calculations
     * @return overtime in minutes
     */
    private int calcAbsenceOvertime(Absence a) {
        int overtime = 0;
        User u = a.getUser();
        SollZeit sollZeit = SollZeitenService.getSollZeitenByUser_Current(u);
        int days;
        DayOfWeek sDay;

        switch (a.getAbsenceType()) {
            case HOLIDAY:
                break;
            case TIME_COMPENSATION:
                days = (int) (a.getStartTime().until(a.getEndTime(), ChronoUnit.DAYS) + 1);
                sDay = a.getStartTime().getDayOfWeek();

                for (int i = 0; i < days; i++, sDay.plus(1)) {
                    int diff = 0;
                    if (i == 0 || i == (days - 1)) {
                        if (a.getStartTime().toLocalTime().isAfter(sollZeit.getSollStartTime(sDay)) && a.getEndTime().toLocalTime().isBefore(sollZeit.getSollEndTime(sDay))) {
                            diff = (int) a.getStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
                        } else if (a.getEndTime().toLocalTime().isBefore(sollZeit.getSollStartTime(sDay)) || a.getStartTime().toLocalTime().isAfter(sollZeit.getSollEndTime(sDay))) {
                        } else if (a.getStartTime().toLocalTime().isAfter(sollZeit.getSollStartTime(sDay))) {
                            diff = (int) a.getStartTime().until(sollZeit.getSollEndTime(sDay), ChronoUnit.MINUTES);
                        } else if (a.getEndTime().toLocalTime().isBefore(sollZeit.getSollEndTime(sDay))) {
                            diff = (int) sollZeit.getSollStartTime(sDay).until(a.getEndTime(), ChronoUnit.MINUTES);
                        } else {
                            diff = (int) sollZeit.getSollStartTime(sDay).until(sollZeit.getSollEndTime(sDay), ChronoUnit.MINUTES);
                        }
                    } else {
                        diff = (int) sollZeit.getSollStartTime(sDay).until(sollZeit.getSollEndTime(sDay), ChronoUnit.MINUTES);
                    }
                    if (diff > 6 * 60) {
                        diff -= 30;
                    }
                    overtime -= diff;

                }
                break;
            case MEDICAL_LEAVE:
                days = (int) (a.getStartTime().until(a.getEndTime(), ChronoUnit.DAYS) + 1);
                sDay = a.getStartTime().getDayOfWeek();

                for (int i = 0; i < days; i++, sDay.plus(1)) {
                    int diff = 0;
                    if (i == 0 || i == (days - 1)) {
                        if (a.getStartTime().toLocalTime().isAfter(sollZeit.getSollStartTime(sDay)) && a.getEndTime().toLocalTime().isBefore(sollZeit.getSollEndTime(sDay))) {
                            diff = (int) a.getStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
                        } else if (a.getEndTime().toLocalTime().isBefore(sollZeit.getSollStartTime(sDay)) || a.getStartTime().toLocalTime().isAfter(sollZeit.getSollEndTime(sDay))) {
                        } else if (a.getStartTime().toLocalTime().isAfter(sollZeit.getSollStartTime(sDay))) {
                            diff = (int) a.getStartTime().until(sollZeit.getSollEndTime(sDay), ChronoUnit.MINUTES);
                        } else if (a.getEndTime().toLocalTime().isBefore(sollZeit.getSollEndTime(sDay))) {
                            diff = (int) sollZeit.getSollStartTime(sDay).until(a.getEndTime(), ChronoUnit.MINUTES);
                        } else {
                            diff = (int) sollZeit.getSollStartTime(sDay).until(sollZeit.getSollEndTime(sDay), ChronoUnit.MINUTES);
                        }
                    } else {
                        diff = (int) sollZeit.getSollStartTime(sDay).until(sollZeit.getSollEndTime(sDay), ChronoUnit.MINUTES);
                    }
                    if (diff > 6 * 60) {
                        diff -= 30;
                    }
                    overtime += diff;
                }

                break;
        }
        return overtime;
    }

    public void onAbsenceSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
        if (selectEvent.getObject() instanceof AbsenceEvent) {
            absenceEvent = (AbsenceEvent) selectEvent.getObject();

            reason = "";

            if (absenceEvent != null) {

                reason = absenceEvent.getAbsence().getReason();
                RequestContext.getCurrentInstance().execute("PF('eventDialog').show();");
            }
        } else {
            RequestContext.getCurrentInstance().execute(("PF('feiertagDialog').show();"));
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

        BenutzerverwaltungService.getUserList().forEach((u) -> {
            allUsers.add(u.getUsername());
        });

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

        this.acknowledgementModel.removeAllEvents();
        this.acknowledgementModel.loadEvents();
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
        if (type == null || type.equals(AbsenceTypeNew.HOLIDAY)) {
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

    public Date getAllowedStartDateToday() {

        if (!AccessRightsService.checkPermission(currentUser.getAccessLevel(), "ALL")) {
            LocalDateTime start = LocalDateTime.now();

            // 22.01.2018 -> Zeit zur Ist-Zeit-Eintragrung von 1ner Woche auf 5Wochen erhöht
            // start = start.withHour(0).withMinute(0).withSecond(0).minusWeeks(30);
            // 09.05.2018 Implementierung, dass Zeiten nur mehr im aktuellen Monat eingetragen werden können!
            start = start.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

            return TimeConverterService.convertLocalDateTimeToDate(start);
        } else {
            return null;
        }
    }

    public Date getAllowedEndDateToday() {

        if (!AccessRightsService.checkPermission(currentUser.getAccessLevel(), "ALL")) {
            LocalDateTime end = LocalDateTime.now();

            end = end.withHour(23).withMinute(59).withSecond(59);

            return TimeConverterService.convertLocalDateTimeToDate(end);
        } else {
            return null;
        }
    }

    public double getOvertimeleft() {
        if (currentUser != null) {
            return currentUser.getOverTimeLeft();
        }
        return 0;
    }

    public void setOvertimeleft(double overtimeleft) {
        this.overtimeleft = overtimeleft;
    }

    public String loadAllTimes() {
        this.selectedUser = "All";
        this.allUsers = new ArrayList<>();

        allUsers.add("All");

        BenutzerverwaltungService.getUserList().forEach((u) -> {
            allUsers.add(u.getUsername());
        });

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

    public void dateSelected(SelectEvent e) {
        //RequestContext.getCurrentInstance().execute("PrimeFaces.info('dateSelected: " + this.dateChanged + "');");
        this.dateChanged = true;
    }

    public void handleClose(CloseEvent event) {
        this.dateChanged = false;
    }

    public boolean isIsAdminInputingOtherPeoplesAbsences() {
        return isAdminInputingOtherPeoplesAbsences;
    }

    public void setIsAdminInputingOtherPeoplesAbsences(boolean isAdminInputingOtherPeoplesAbsences) {
        this.isAdminInputingOtherPeoplesAbsences = isAdminInputingOtherPeoplesAbsences;
    }

    public String getUserWhoseAbsenceTheAdminInputsInStringFormat() {
        return userWhoseAbsenceTheAdminInputsInStringFormat;
    }

    public void setUserWhoseAbsenceTheAdminInputsInStringFormat(String userWhoseAbsenceTheAdminInputsInStringFormat) {
        this.userWhoseAbsenceTheAdminInputsInStringFormat = userWhoseAbsenceTheAdminInputsInStringFormat;
    }

    public boolean isUserAbleToInputOtherPeoplesAbsences() {
        if (AccessRightsService.checkPermission(this.currentUser.getAccessLevel(), "ALL")) {
            return true;
        }
        return false;
    }

    public void onLoad(ActionEvent ev) {
        this.allUsers = new ArrayList<>();
        this.allUsers.add("-");
        BenutzerverwaltungService.getUserList().forEach((u) -> {
            allUsers.add(u.getUsername());
        });
    }

    public String getUserStringForUserWhoseAbsenceAdminInputs() {
        if (userWhoseAbsenceTheAdminInputsInStringFormat != null) {
            if (!userWhoseAbsenceTheAdminInputsInStringFormat.equals("-")) {
                User u = BenutzerverwaltungService.getUser(this.userWhoseAbsenceTheAdminInputsInStringFormat);
                return u.getUsername() + ": " + u.getPersName();
            }
        }
        return "";

    }
}
