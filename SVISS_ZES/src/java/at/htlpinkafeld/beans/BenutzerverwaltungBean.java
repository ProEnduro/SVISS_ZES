/*
 * To selectedUser this license header, choose License Headers in Project Properties.
 * To selectedUser this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.pojo.SollZeiten;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserProxy;
import at.htlpinkafeld.service.AccessRightsService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.IstZeitService;
import at.htlpinkafeld.service.SollZeitenService;
import at.htlpinkafeld.service.TimeConverterService;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
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

    private List<SollZeiten> currentSollZeiten;
    private List<SollZeiten> newSollZeiten;

    private ScheduleModel timeModel;
    private ScheduleEvent curEvent;

    private LocalDate pointDate;

    private Double weekTime;

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

    public ScheduleModel getTimeModel() {
        return timeModel;
    }

    public List<User> getUserList() {
        userlist = BenutzerverwaltungService.getUserList();
        return userlist;
    }

    public void newUser() {
        selectedUser = new UserProxy();
        newSollZeiten = new ArrayList<>();
    }

    public void editUser(ActionEvent e) {
        selectedUser = new UserProxy((User) e.getComponent().getAttributes().get("user"));
    }

    public void saveUser() {
        if (selectedUser.getUserNr() == -1) {
            userlist.add(selectedUser);
            BenutzerverwaltungService.insertUser(selectedUser);
        } else {
            userlist.remove(selectedUser);
            userlist.add(selectedUser);
            BenutzerverwaltungService.updateUser(selectedUser);
        }
        if (!newSollZeiten.isEmpty()) {
            for (SollZeiten sz : newSollZeiten) {
                if (currentSollZeiten.contains(sz)) {
                    SollZeitenService.updateZeit(sz);
                } else {
                    SollZeitenService.insertZeit(sz);
                }
            }
            for (SollZeiten sz : currentSollZeiten) {
                if (!newSollZeiten.contains(sz)) {
                    SollZeitenService.deleteZeit(sz);
                }
            }
        }
        selectedUser = null;
    }

    //WeekTime Stuff
    public void saveTimes() {
        selectedUser.setWeekTime(getNewWeekTime());
//        for (SollZeiten sz : newSollZeiten) {
//            if (currentSollZeiten.contains(sz)) {
//                SollZeitenService.updateZeit(sz);
//            } else {
//                SollZeitenService.insertZeit(sz);
//            }
//        }
//        for (SollZeiten sz : currentSollZeiten) {
//            if (!newSollZeiten.contains(sz)) {
//                SollZeitenService.deleteZeit(sz);
//            }
//        }
    }

    public void discardTimes() {
        newSollZeiten = new ArrayList<>();
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
        return AccessRightsService.AccessGroups;
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
        for (SollZeiten sz : newSollZeiten) {
            dayTime = sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES) / 60.0;
            if (dayTime >= 6) {
                dayTime -= 0.5;
            }
            wt += dayTime;
        }
        return wt;
    }

    public void setNewWeekTime(Double weekTime) {
        this.weekTime = weekTime;
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

        for (int i = 0; i < 5; i++) {
            LocalDateTime sDateTime = LocalDateTime.of(pointDate.plusDays(i), startTime);
            curEvent = new DefaultScheduleEvent("", TimeConverterService.convertLocalDateTimeToDate(sDateTime), TimeConverterService.convertLocalDateTimeToDate(sDateTime.with(endTime)));
            SollZeiten sz = new SollZeiten(sDateTime.getDayOfWeek(), selectedUser, startTime, endTime);
            timeModel.addEvent(curEvent);
            newSollZeiten.add(sz);
        }
    }

    public void loadSollZeiten() {
        timeModel.clear();
        currentSollZeiten = SollZeitenService.getSollZeitenByUser(selectedUser);
        newSollZeiten = new LinkedList<>();
        for (SollZeiten sz : currentSollZeiten) {
            newSollZeiten.add(new SollZeiten(sz));
        }
        for (SollZeiten sz : newSollZeiten) {
            LocalDate curDate = pointDate.with(TemporalAdjusters.firstInMonth(sz.getDay()));
            timeModel.addEvent(new DefaultScheduleEvent("", TimeConverterService.convertLocalTimeToDate(curDate, sz.getSollStartTime()),
                    TimeConverterService.convertLocalDateTimeToDate(LocalDateTime.of(curDate, sz.getSollEndTime())), curDate.getDayOfWeek()));
        }
    }

    public void onEventSelect(SelectEvent selectEvent) {
        curEvent = (ScheduleEvent) selectEvent.getObject();
    }

    public void onDateSelect(SelectEvent selectEvent) {
        Date date = (Date) selectEvent.getObject();
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.add(Calendar.HOUR_OF_DAY, 2);
        curEvent = new DefaultScheduleEvent("", date, c.getTime(), TimeConverterService.convertDateToLocalDateTime(date).getDayOfWeek());
    }

    public void addEvent() {
        LocalDateTime sDateTime = LocalDateTime.of(pointDate.with(TemporalAdjusters.firstInMonth((DayOfWeek) curEvent.getData())), TimeConverterService.convertDateToLocalTime(curEvent.getStartDate()));
        ((DefaultScheduleEvent) curEvent).setEndDate(TimeConverterService.convertLocalDateTimeToDate(sDateTime.plus(Duration.ofMillis(curEvent.getEndDate().getTime() - curEvent.getStartDate().getTime()))));
        ((DefaultScheduleEvent) curEvent).setStartDate(TimeConverterService.convertLocalDateTimeToDate(sDateTime));

        SollZeiten sz = new SollZeiten(sDateTime.getDayOfWeek(), selectedUser, sDateTime.toLocalTime(), TimeConverterService.convertDateToLocalTime(curEvent.getEndDate()));
        if (curEvent.getId() == null) {
            timeModel.addEvent(curEvent);
        } else {
            timeModel.updateEvent(curEvent);
        }
        newSollZeiten.remove(sz);
        newSollZeiten.add(sz);
        curEvent = new DefaultScheduleEvent();
    }

    public void removeEvent() {
        if (curEvent.getId() != null) {
            SollZeiten sz = new SollZeiten((DayOfWeek) curEvent.getData(), selectedUser, null, null);
            newSollZeiten.remove(sz);
        }
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        LocalDateTime startDateTime = TimeConverterService.convertDateToLocalDateTime(event.getScheduleEvent().getStartDate());
        LocalTime endTime = TimeConverterService.convertDateToLocalTime(event.getScheduleEvent().getEndDate());
        Calendar c = Calendar.getInstance();
        c.setTime(event.getScheduleEvent().getStartDate());
        for (SollZeiten sz : newSollZeiten) {
            if (sz.getDay().equals(startDateTime.getDayOfWeek())) {
                sz.setSollEndTime(endTime);
            }
        }
    }

    //Approver Stuff from here on
    public void editApprover(ActionEvent e) {

        selectedUser = (User) e.getComponent().getAttributes().get("user");
        List<User> source = new LinkedList<>();
        for (AccessLevel al : AccessRightsService.AccessGroups) {
            if (al.containsPermission("ACKNOWLEDGE_USERS") || al.containsPermission("ALL")) {
                source.addAll(BenutzerverwaltungService.getUserByAccessLevel(al));
            }
        }
        source.remove(selectedUser);

        List<User> target = new LinkedList<>();
        for (User appr : selectedUser.getApprover()) {
            target.add(appr);
        }
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
