/*
 * To selectedUser this license header, choose License Headers in Project Properties.
 * To selectedUser this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserProxy;
import at.htlpinkafeld.service.AccessRightsService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.EmailService;
import at.htlpinkafeld.service.PasswordEncryptionService;
import at.htlpinkafeld.service.SollZeitenService;
import at.htlpinkafeld.service.TimeConverterService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.DualListModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author msi
 */
public class BenutzerverwaltungBean {

    private List<User> userlist;

    private User selectedUser;

    private DualListModel<User> approverModel;

    private SollZeit currentSollZeit;
    private SollZeit newSollZeit;

    private ScheduleModel timeModel;
    private ScheduleEvent curEvent;

    private LocalDate pointDate;

    private Double weekTime;

    private String resetPWString;

    @PostConstruct
    public void init() {
        timeModel = new DefaultScheduleModel();
        pointDate = LocalDate.of(2016, 8, 1);
    }

    /**
     * Creates a new instance of BenutzerverwaltungsBean
     */
    public BenutzerverwaltungBean() {
    }

    public void onLoad() {
        userlist = BenutzerverwaltungService.getUserList();
    }

    public ScheduleModel getTimeModel() {
        return timeModel;
    }

    public List<User> getUserList() {
//        if (userlist == null) {
//            userlist = BenutzerverwaltungService.getUserList();
//        }
        return userlist;
    }

    public void newUser() {
        selectedUser = new UserProxy();
        newSollZeit = null;
    }

    public void editUser(ActionEvent e) {
        selectedUser = new UserProxy((User) e.getComponent().getAttributes().get("user"));
        newSollZeit = null;
    }

    public void saveUser() throws FileNotFoundException, IOException {
        if (isEmailUnavailable(selectedUser.getEmail())) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Action failed", "Diese Email wurde bereits vergeben!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else if (isUsernameUnavailable(selectedUser.getUsername())) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Action failed", "Dieser Benutzername wurde bereits vergeben!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else {
            if (selectedUser.getUserNr() == -1) {
                if (selectedUser.getPass() == null) {
                    resetPWString = getResetPWString();
                    resetPassword();
                }
                userlist.add(selectedUser);
                BenutzerverwaltungService.insertUser(selectedUser);
            } else {
                userlist.remove(selectedUser);
                userlist.add(selectedUser);
                BenutzerverwaltungService.updateUser(selectedUser);
            }

            if (newSollZeit != null) {
                SollZeitenService.insertZeit(newSollZeit);
            }

            ServletContext serv = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            String path = serv.getRealPath("/") + "/resources/";

            File file = new File(path + "themes.properties");

            try (FileInputStream inSF = new FileInputStream(file)) {
                Properties prop = new Properties();
                prop.load(inSF);

                try (FileOutputStream outSF = new FileOutputStream(file)) {
                    prop.setProperty(selectedUser.getUsername(), "delta");
                    prop.store(outSF, "Themes_of_user");
                    outSF.close();
                }
                inSF.close();
            }
            selectedUser = null;
        }
    }

    public String getResetPWString() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    public void setResetPWString(String resetPWString) {
        this.resetPWString = resetPWString;
    }

    public void resetPassword() {
        if (resetPWString.length() < 6) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, "Action failed", "Passwort muss mindestens 6 Zeichen haben!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else {
            selectedUser.setPass(PasswordEncryptionService.digestPassword(resetPWString));
            if (selectedUser.getUserNr() == -1) {
                userlist.add(selectedUser);
//            BenutzerverwaltungService.insertUser(selectedUser);
            } else {
                userlist.remove(selectedUser);
                userlist.add(selectedUser);
//            BenutzerverwaltungService.updateUser(selectedUser);
            }
            try {
                EmailService.sendUserNewPasswordEmail(resetPWString, selectedUser);
            } catch (Exception e) {
                Logger.getLogger(BenutzerverwaltungBean.class.getName()).log(Level.SEVERE, null, e);

            }
        }
    }

    public boolean isUsernameUnavailable(String username) {
        boolean b = false;
        for (User u : userlist) {
            if (!u.equals(selectedUser)) {
                if (u.getUsername().contentEquals(username)) {
                    b = true;
                    break;
                }
            }
        }
        return b;
    }

    public boolean isEmailUnavailable(String email) {
        boolean b = false;
        for (User u : userlist) {
            if (!u.equals(selectedUser)) {
                if (u.getEmail().contentEquals(email)) {
                    b = true;
                    break;
                }
            }
        }
        return b;
    }

    //WeekTime Stuff
    public void saveTimes() {
        selectedUser.setWeekTime(getNewWeekTime());
//        for (SollZeit sz : newSollZeiten) {
//            if (currentSollZeiten.contains(sz)) {
//                SollZeitenService.updateZeit(sz);
//            } else {
//                SollZeitenService.insertZeit(sz);
//            }
//        }
//        for (SollZeit sz : currentSollZeiten) {
//            if (!newSollZeiten.contains(sz)) {
//                SollZeitenService.deleteZeit(sz);
//            }
//        }
    }

    public void discardTimes() {
        newSollZeit = null;
    }

    public void discardUserChanges() {
        selectedUser = new UserProxy();
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<AccessLevel> getAccessGroups() {
        return AccessRightsService.getAccessGroups();
    }

    //Scheduel Stuff from this Point
    public Date getPointDate() {
        return TimeConverterService.convertLocalDateTimeToDate(pointDate.atStartOfDay());
    }

    public ScheduleEvent getCurEvent() {
        return curEvent;
    }

    public void setCurEvent(ScheduleEvent curEvent) {
        this.curEvent = curEvent;
    }

    public Double getNewWeekTime() {
        Double wt = 0.0;
        Double dayTime;
        for (DayOfWeek dow : newSollZeit.getSollStartTimeMap().keySet()) {
            dayTime = newSollZeit.getSollStartTime(dow).until(newSollZeit.getSollEndTime(dow), ChronoUnit.MINUTES) / 60.0;
            if (dayTime >= 6) {
                dayTime -= 0.5;
            }
            wt += dayTime;
        }
        return wt;
    }

    public void setNewWeekTime(Double weekTime) {
        this.weekTime = Math.abs(weekTime);
    }

    public void distributeTimes() {
        timeModel.clear();
        discardTimes();
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime;
        if (weekTime >= 30) {
            endTime = startTime.plusMinutes((long) ((weekTime / 5) * 60 + 30));
        } else {
            endTime = startTime.plusMinutes((long) ((weekTime / 5) * 60));
        }

        Map<DayOfWeek, LocalTime> startTimeMap = new HashMap<>();
        Map<DayOfWeek, LocalTime> endTimeMap = new HashMap<>();

        for (int i = 1; i <= 5; i++) {
            LocalDateTime sDateTime = LocalDateTime.of(pointDate.plusDays(i - 1), startTime);
            curEvent = new DefaultScheduleEvent("", TimeConverterService.convertLocalDateTimeToDate(sDateTime), TimeConverterService.convertLocalDateTimeToDate(sDateTime.with(endTime)), sDateTime.getDayOfWeek());
            timeModel.addEvent(curEvent);
            startTimeMap.put(DayOfWeek.of(i), startTime);
            endTimeMap.put(DayOfWeek.of(i), endTime);
        }
        newSollZeit = new SollZeit(selectedUser, startTimeMap, endTimeMap);

    }

    public void loadSollZeiten() {
        timeModel.clear();
        currentSollZeit = SollZeitenService.getSollZeitenByUser_Current(selectedUser);
        if (currentSollZeit != null) {
            newSollZeit = new SollZeit(currentSollZeit);

            newSollZeit.getSollEndTimeMap().keySet().forEach((dow) -> {
                LocalDate curDate = pointDate.with(TemporalAdjusters.firstInMonth(dow));
                timeModel.addEvent(new DefaultScheduleEvent("", TimeConverterService.convertLocalTimeToDate(curDate, newSollZeit.getSollStartTime(dow)),
                        TimeConverterService.convertLocalDateTimeToDate(LocalDateTime.of(curDate, newSollZeit.getSollEndTime(dow))), dow));
            });
        } else {
            newSollZeit = new SollZeit(selectedUser, new HashMap<>(), new HashMap<>());
        }

    }

    public void onEventSelect(SelectEvent selectEvent) {
        RequestContext.getCurrentInstance().execute("PF('eventDialog').hide();");
        curEvent = (ScheduleEvent) selectEvent.getObject();
    }

    public void onDateSelect(SelectEvent selectEvent) {
        RequestContext.getCurrentInstance().execute("PF('eventDialog').hide();");
        Date date = (Date) selectEvent.getObject();
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        if (newSollZeit.getSollStartTimeMap().containsKey(TimeConverterService.convertDateToLocalDate(date).getDayOfWeek())) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Action failed", "Nur eine Arbeitszeit pro Tag!"));
            FacesContext.getCurrentInstance().validationFailed();
        }

        if (!FacesContext.getCurrentInstance().isValidationFailed()) {
            c.add(Calendar.HOUR_OF_DAY, 2);
            curEvent = new DefaultScheduleEvent("", date, c.getTime(), TimeConverterService.convertDateToLocalDateTime(date).getDayOfWeek());
        }
    }

    public void addSollZeitEvent() {
        LocalDateTime sDateTime = LocalDateTime.of(pointDate.with(TemporalAdjusters.firstInMonth((DayOfWeek) curEvent.getData())), TimeConverterService.convertDateToLocalTime(curEvent.getStartDate()));

        Date endDate = TimeConverterService.convertLocalDateTimeToDate(sDateTime.plus(Duration.ofMillis(curEvent.getEndDate().getTime() - curEvent.getStartDate().getTime())));
        Date startDate = TimeConverterService.convertLocalDateTimeToDate(sDateTime);

        if (startDate.after(endDate)) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Action failed", "Endzeitpunkt ist vor Startzeitpunkt!"));
            FacesContext.getCurrentInstance().validationFailed();
        } else {

            ((DefaultScheduleEvent) curEvent).setEndDate(endDate);
            ((DefaultScheduleEvent) curEvent).setStartDate(startDate);

            if (curEvent.getId() == null) {
                timeModel.addEvent(curEvent);
            } else {
                timeModel.updateEvent(curEvent);
            }
            newSollZeit.getSollStartTimeMap().put(sDateTime.getDayOfWeek(), sDateTime.toLocalTime());
            newSollZeit.getSollEndTimeMap().put(sDateTime.getDayOfWeek(), TimeConverterService.convertDateToLocalTime(endDate));
            curEvent = new DefaultScheduleEvent();
        }
    }

    public void removeSollZeitEvent() {
        if (curEvent.getId() != null) {
            DayOfWeek dow = (DayOfWeek) curEvent.getData();
            newSollZeit.getSollStartTimeMap().remove(dow);
            newSollZeit.getSollEndTimeMap().remove(dow);

        }
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        LocalDateTime startDateTime = TimeConverterService.convertDateToLocalDateTime(event.getScheduleEvent().getStartDate());
        LocalTime endTime = TimeConverterService.convertDateToLocalTime(event.getScheduleEvent().getEndDate());
        if (endTime.isBefore(startDateTime.toLocalTime())) {
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Action failed", "Zeit ist über mehr als einen Tag eingetragen!"));
            FacesContext.getCurrentInstance().validationFailed();
            Calendar c = Calendar.getInstance();
            c.setTime(event.getScheduleEvent().getEndDate());
            c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) - event.getMinuteDelta());
            ((DefaultScheduleEvent) event.getScheduleEvent()).setEndDate(c.getTime());
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(event.getScheduleEvent().getStartDate());

            newSollZeit.getSollEndTimeMap().put(startDateTime.getDayOfWeek(), endTime);
        }
    }

    //Approver Stuff from here on
    public void editApprover(ActionEvent e) {

        selectedUser = (User) e.getComponent().getAttributes().get("user");
        List<User> source = new LinkedList<>();
        AccessRightsService.getAccessGroups().stream().filter((al) -> (al.containsPermission("ACKNOWLEDGE_USERS") || al.containsPermission("ALL"))).forEachOrdered((al) -> {
            source.addAll(BenutzerverwaltungService.getUserByAccessLevel(al));
        });
        source.remove(selectedUser);

        List<User> target = new LinkedList<>();
        selectedUser.getApprover().forEach((appr) -> {
            target.add(appr);
        });
        source.removeAll(selectedUser.getApprover());
        approverModel = new DualListModel<>(source, target);
    }

    public DualListModel<User> getApproverModel() {
        return approverModel;
    }

    public void setApproverModel(DualListModel<User> approverModel) {
        this.approverModel = approverModel;
    }

    public void saveApprover() {
        selectedUser.setApprover(approverModel.getTarget());
        BenutzerverwaltungService.updateApproverOfUser(selectedUser);
        selectedUser = null;
    }

}
