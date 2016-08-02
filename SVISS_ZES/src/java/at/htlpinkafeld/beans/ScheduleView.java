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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

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

            switch (this.type) {
                case 1:
                    e.setStyleClass(types.get(type - 1).getAbsenceName().replace(" ", "_"));
                    DAOFactory.getDAOFactory().getAbsenceDAO().insert(new Absence(this.currentUser, types.get(type - 1), e.getStartDate(), e.getEndDate()));
                    break;
                case 2:
                    e.setStyleClass(types.get(type - 1).getAbsenceName().replace(" ", "_"));
                    DAOFactory.getDAOFactory().getAbsenceDAO().insert(new Absence(this.currentUser, types.get(type - 1), e.getStartDate(), e.getEndDate()));
                    break;
                case 3:
                    e.setStyleClass(types.get(type - 1).getAbsenceName().replace(" ", "_"));
                    DAOFactory.getDAOFactory().getAbsenceDAO().insert(new Absence(this.currentUser, types.get(type - 1), e.getStartDate(), e.getEndDate()));
                    break;
                case 4:
                    e.setStyleClass(types.get(type - 1).getAbsenceName().replace(" ", "_"));
                    DAOFactory.getDAOFactory().getAbsenceDAO().insert(new Absence(this.currentUser, types.get(type - 1), e.getStartDate(), e.getEndDate()));
                    break;
            }

            eventModel.addEvent(e);
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
            WorkTimeEvent e = new WorkTimeEvent(event.getTitle(), event.getStartDate(), event.getEndDate(), new WorkTime(currentUser, this.event.getStartDate(), this.event.getEndDate(), 0, "", ""));

            e.setStyleClass("istzeit");
            IstZeitService.addIstTime(e.getWorktime());

            eventModel.addEvent(e);
        } else {
            

            if (event instanceof WorkTimeEvent) {
                WorkTime time = ((WorkTimeEvent)event).getWorktime();
                
                time.setStartTime(event.getStartDate());
                time.setEndTime(event.getEndDate());
                

                IstZeitService.update(time);
            } else if (event instanceof AbsenceEvent) {
                System.out.println(((AbsenceEvent) event).getAbsence());
                
                AbsenceService.updateAbsence(((AbsenceEvent) event).getAbsence());
            }

            eventModel.updateEvent(event);
        }

        event = new DefaultScheduleEvent();
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
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

        }

        this.reloadAcknowledgements(actionEvent);

        absenceEvent = new AbsenceEvent();
    }

    public void onAbsenceSelect(SelectEvent selectEvent) {
        absenceEvent = (AbsenceEvent) selectEvent.getObject();
    }

    public AbsenceEvent getAbsenceEvent() {
        return absenceEvent;
    }

    public void setAbsenceEvent(AbsenceEvent event) {
        this.absenceEvent = event;
    }

    public void reloadAcknowledgements(ActionEvent event) {
        acknowledgementModel.clear();

        for (Absence a : AbsenceService.getAbsenceByUserAndUnacknowledged(currentUser)) {

            AbsenceEvent e = new AbsenceEvent(a.getReason(), a.getStartTime(), a.getEndTime(), a);

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
        
        for(User u: BenutzerverwaltungService.getUserList()){
            allUsers.add(u.getUsername());
        }
    }

    public void reloadAbwesenheiten(ActionEvent event) {

        this.eventModel.clear();
        List<Absence> absenceList = AbsenceService.getAbsenceByUser(currentUser);
        DefaultScheduleEvent e;

        for (Absence a : absenceList) {
            e = new AbsenceEvent(a.getReason(), a.getStartTime(), a.getEndTime(), a);

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

        int i = 0;

        for (WorkTime w : worktimelist) {
            System.out.println(w);
            eventModel.addEvent(new WorkTimeEvent(String.valueOf(i++), w.getStartTime(), w.getEndTime(), "istzeit", w));
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
    
    
    public void loadAbwesenheitByUser(ActionEvent ev){
        
        acknowledgementModel.clear();

        for (Absence a : AbsenceService.getAllUnacknowledged()) {
            

            AbsenceEvent e = new AbsenceEvent(a.getReason(), a.getStartTime(), a.getEndTime(), a);

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
