/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Martin Six
 */
public class Absence {
    private final int absenceID;
    private final User user;
    private final AbsenceType absenceType;
    private Date startTime;
    private Date endTime;
    private boolean acknowledged;

    public Absence(int absenceID, User user, AbsenceType absenceType, Date startTime, Date endTime, boolean acknowledged) {
        this.absenceID = absenceID;
        this.user = user;
        this.absenceType = absenceType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.acknowledged = acknowledged;
    }

    public Absence(int absenceID, User user, AbsenceType absenceType, Date startTime, Date endTime) {
        this.absenceID = absenceID;
        this.user = user;
        this.absenceType = absenceType;
        this.startTime = startTime;
        this.endTime = endTime;
        acknowledged=false;
    }

    public int getAbsenceID() {
        return absenceID;
    }

    public User getUser() {
        return user;
    }

    public AbsenceType getAbsenceType() {
        return absenceType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    @Override
    public String toString() {
        return "Absence{" + "absenceID=" + absenceID + ", user=" + user + ", absenceType=" + absenceType + ", startTime=" + startTime + ", endTime=" + endTime + ", acknowledged=" + acknowledged + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.absenceID;
        hash = 59 * hash + Objects.hashCode(this.user);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Absence other = (Absence) obj;
        if (this.absenceID != other.absenceID) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }
    
}
