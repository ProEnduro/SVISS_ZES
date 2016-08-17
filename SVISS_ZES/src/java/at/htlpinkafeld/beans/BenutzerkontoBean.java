/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.pojo.SollZeiten;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import at.htlpinkafeld.service.IstZeitService;
import at.htlpinkafeld.service.PasswordEncryptionService;
import at.htlpinkafeld.service.SollZeitenService;
import at.htlpinkafeld.service.TimeConverterService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.apache.commons.codec.digest.DigestUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author ÐarkHell2
 */
@FacesValidator("oldPasswordValidator")
public class BenutzerkontoBean implements Validator {

    String themeTest = "1";

    FacesContext context = FacesContext.getCurrentInstance();
    MasterBean masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);

    ScheduleModel sollzeitModel;
    User user;
    LocalDate pointDate;
    ScheduleEvent curEvent;

    private String newPw = "";

    @PostConstruct
    public void init() {
        pointDate = LocalDate.of(2016, 8, 1);
    }

    public BenutzerkontoBean() {
        user = masterBean.getUser();
    }

    public void onLoad() {
        user = masterBean.getUser();
        loadSollZeiten();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void saveUser() {
        BenutzerverwaltungService.updateUser(user);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("User gespeichert!", ""));
    }

    public String getNewPw() {
        return newPw;
    }

    public void setNewPw(String newPw) {
        this.newPw = newPw;
    }

    public void discardPasswordChanges() {
        newPw = "";
    }

    public void savePassword() {
        user.setPass(PasswordEncryptionService.digestPassword(newPw));
        BenutzerverwaltungService.updateUser(user);
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

        List<SollZeiten> currentSollZeiten;

        currentSollZeiten = SollZeitenService.getSollZeitenByUser(user);

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

    @Override
    public void validate(FacesContext fc, UIComponent uic, Object o) throws ValidatorException {
        if (!o.toString().contentEquals(user.getPass())) {
            throw new ValidatorException(new FacesMessage("Altes Passwort stimmt nicht", ""));
        }
    }
}
