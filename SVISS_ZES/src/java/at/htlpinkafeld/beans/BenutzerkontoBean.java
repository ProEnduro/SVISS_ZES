/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.pojo.SollZeiten;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserProxy;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.IstZeitService;
import at.htlpinkafeld.service.SollZeitenService;
import at.htlpinkafeld.service.TimeConverterService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author ÐarkHell2
 */
public class BenutzerkontoBean {

    List<User> userlist;
    String persName;
    String userName;
    String email;
    Date hireDate;
    String password;
    double weekTime;

    String themeTest = "1";

    FacesContext context = FacesContext.getCurrentInstance();
    MasterBean masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);
    User user;

    ScheduleModel sollzeitModel;
    User currentUser;
    LocalDate pointDate;
    ScheduleEvent curEvent;

    @PostConstruct
    public void init() {
        pointDate = LocalDate.of(2016, 8, 1);

        loadSollZeiten();
    }

    public BenutzerkontoBean() {
        user = new UserProxy();
        user = masterBean.getUser();
    }

    public List<User> getUserList() {
        return BenutzerverwaltungService.getUserList();
    }

    public AccessLevel getAccessLevel() {
        return user.getAccessLevel();
    }

    public String getPersName() {
        return user.getPersName();
    }

    public void setPersName(String persName) {
        user.setPersName(persName);
    }

    public String getUserName() {
        return user.getUsername();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public void setEmail(String email) {
        user.setEmail(email);
    }

    public LocalDate getHireDate() {
        return user.getHiredate();
    }

    public String getPassword() {
        return user.getPass();
    }

    public void setPassword(String password) {
        user.setPass(password);
    }

    public double getWeekTime() {
        return user.getWeekTime();
    }

    public void saveUser() {
        BenutzerverwaltungService.updateUser(user);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("User gespeichert!", ""));
    }

    public ScheduleModel getSollzeitModel() {
        return sollzeitModel;
    }

    public void setSollzeitModel(ScheduleModel sollzeitModel) {
        this.sollzeitModel = sollzeitModel;
    }

    public ScheduleEvent getCurEvent() {
        return curEvent;
    }

    public void setCurEvent(ScheduleEvent curEvent) {
        this.curEvent = curEvent;
    }

    public void loadSollZeiten() {
        this.sollzeitModel = new DefaultScheduleModel();

        currentUser = masterBean.getUser();
        System.out.println(currentUser);

        currentUser = BenutzerverwaltungService.getUser(currentUser.getUsername());
        System.out.println(currentUser);

        List<SollZeiten> currentSollZeiten;

        currentSollZeiten = SollZeitenService.getSollZeitenByUser(currentUser);

        for (SollZeiten sz : currentSollZeiten) {
            LocalDate curDate = pointDate.with(TemporalAdjusters.firstInMonth(sz.getDay()));
            DefaultScheduleEvent de = new DefaultScheduleEvent("", TimeConverterService.convertLocalTimeToDate(curDate, sz.getSollStartTime()),
                    TimeConverterService.convertLocalDateTimeToDate(LocalDateTime.of(curDate, sz.getSollEndTime())), curDate.getDayOfWeek());
            sollzeitModel.addEvent(de);
        }
    }

    public void onEventSelect(SelectEvent selectEvent) {
        curEvent = (ScheduleEvent) selectEvent.getObject();
    }

    public Date getPointDate() {
        return IstZeitService.convertLocalDateTimeToDate(pointDate.atStartOfDay());
    }
}
