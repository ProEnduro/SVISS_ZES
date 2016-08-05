/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.service.IstZeitService;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.faces.context.FacesContext;

/**
 *
 * @author ÐarkHell2
 */
public class IstZeitBean {

    private Date startTime;
    private Date endTime;

    public IstZeitBean() {
    }

    public IstZeitBean(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void addIstTime() {
        FacesContext context = FacesContext.getCurrentInstance();
        MasterBean masterBean = (MasterBean) context.getApplication().evaluateExpressionGet(context, "#{masterBean}", MasterBean.class);
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        if (startTime != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, startTime.getHours());
            cal.set(Calendar.MINUTE, startTime.getMinutes());
            cal.set(Calendar.SECOND, 0);

            this.startTime = cal.getTime();
        }
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        if (endTime != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, endTime.getHours());
            cal.set(Calendar.MINUTE, endTime.getMinutes());
            cal.set(Calendar.SECOND, 0);

            this.endTime = cal.getTime();
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.startTime);
        hash = 79 * hash + Objects.hashCode(this.endTime);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IstZeitBean other = (IstZeitBean) obj;
        if (!Objects.equals(this.startTime, other.startTime)) {
            return false;
        }
        if (!Objects.equals(this.endTime, other.endTime)) {
            return false;
        }
        return true;
    }

}
