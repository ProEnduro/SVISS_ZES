/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import at.htlpinkafeld.mobileInterface.service.util.LocalDateTimeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Martin Six
 */
@XmlRootElement
public class Absence implements Serializable {

    private Integer absenceID;
    private User user;
    private AbsenceType absenceType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;
    private boolean acknowledged;

    public Absence() {
        absenceID = -1;
    }

    public Absence(Absence a) {
        this.absenceID = a.absenceID;
        this.user = a.user;
        this.absenceType = a.absenceType;
        this.startTime = a.startTime;
        this.endTime = a.endTime;
        this.reason = a.reason;
        this.acknowledged = a.acknowledged;
    }

    /**
     * Constructor using all Attributes. It is deprecated because it disables
     * automatic id distribution.
     *
     * @param absenceID
     * @param user
     * @param absenceType
     * @param startTime
     * @param endTime
     * @param reason
     * @param acknowledged
     * @deprecated
     */
    @Deprecated
    public Absence(Integer absenceID, User user, AbsenceType absenceType, LocalDateTime startTime, LocalDateTime endTime, String reason, Boolean acknowledged) {
        this.absenceID = absenceID;
        this.user = user;
        this.absenceType = absenceType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.acknowledged = acknowledged;
    }

    public Absence(User user, AbsenceType absenceType, LocalDateTime startTime, LocalDateTime endTime, String reason) {
        this();
        this.user = user;
        this.absenceType = absenceType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.acknowledged = false;
    }

    public Absence(User user, AbsenceType absenceType, LocalDateTime startTime, LocalDateTime endTime) {
        this();
        this.user = user;
        this.absenceType = absenceType;
        this.startTime = startTime;
        this.endTime = endTime;
        reason = absenceType.getAbsenceName();
        acknowledged = false;
    }

    public Integer getAbsenceID() {
        return absenceID;
    }

    public void setAbsenceID(Integer absenceID) {
        this.absenceID = absenceID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AbsenceType getAbsenceType() {
        return absenceType;
    }

    public void setAbsenceType(AbsenceType absenceType) {
        this.absenceType = absenceType;
    }

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    @Override
    public String toString() {
        return "Absence{" + "absenceID=" + absenceID + ", user=" + user + ", absenceType=" + absenceType + ", startTime=" + startTime + ", endTime=" + endTime + ", reason=" + reason + ", acknowledged=" + acknowledged + '}';
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
        if (!Objects.equals(this.absenceID, other.absenceID)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

}
