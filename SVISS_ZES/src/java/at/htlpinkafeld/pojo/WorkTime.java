/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import at.htlpinkafeld.mobileInterface.service.util.LocalDateTimeAdapter;
import at.htlpinkafeld.mobileInterface.service.util.LocalTimeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Martin Six
 */
@XmlRootElement
public class WorkTime implements Serializable {

    private static final long serialVersionUID = 6379762663436317117L;

    private Integer timeID;
    private User user;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalTime sollStartTime;
    private LocalTime sollEndTime;
    //Break Time in minutes
    private Integer breakTime;
    private String startComment;
    private String endComment;

    public WorkTime() {
        timeID = -1;
    }

    public WorkTime(WorkTime wt) {
        this.timeID = wt.timeID;
        this.user = wt.user;
        this.startTime = wt.startTime;
        this.endTime = wt.endTime;
        this.breakTime = wt.breakTime;
        this.startComment = wt.startComment;
        this.endComment = wt.endComment;
        this.sollStartTime = wt.sollStartTime;
        this.sollEndTime = wt.sollEndTime;
    }

    @Deprecated
    public WorkTime(Integer timeID, User user, LocalDateTime startTime, LocalDateTime endTime, LocalTime sollStartTime, LocalTime sollEndTime, Integer breakTime, String startComment, String endComment) {
        this.timeID = timeID;
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sollStartTime = sollStartTime;
        this.sollEndTime = sollEndTime;
        this.breakTime = breakTime;
        this.startComment = startComment;
        this.endComment = endComment;
    }

    public WorkTime(User user, LocalDateTime startTime, LocalDateTime endTime, Integer breakTime, String startComment, String endComment) {
        this();
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.breakTime = breakTime;
        this.startComment = startComment;
        this.endComment = endComment;
    }

    public WorkTime(User user, LocalDateTime startTime) {
        this();
        this.user = user;
        this.startTime = startTime;
    }

    public Integer getTimeID() {
        return timeID;
    }

    public void setTimeID(Integer timeID) {
        this.timeID = timeID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    public LocalTime getSollStartTime() {
        return sollStartTime;
    }

    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    public void setSollStartTime(LocalTime sollStartTime) {
        this.sollStartTime = sollStartTime;
    }

    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    public LocalTime getSollEndTime() {
        return sollEndTime;
    }

    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    public void setSollEndTime(LocalTime sollEndTime) {
        this.sollEndTime = sollEndTime;
    }

    public Integer getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(Integer breakTime) {
        this.breakTime = breakTime;
    }

    public String getStartComment() {
        return startComment;
    }

    public void setStartComment(String startComment) {
        this.startComment = startComment;
    }

    public String getEndComment() {
        return endComment;
    }

    public void setEndComment(String endComment) {
        this.endComment = endComment;
    }

    /**
     *
     * @return WorkTime after 19:00 in Minutes
     */
    public double getOvertimeAfter19() {
        long ot = LocalTime.of(19, 0).until(endTime, ChronoUnit.MINUTES);
        if (ot >= 0) {
            return ot;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "WorkTime{" + "timeID=" + timeID + ", user=" + user + ", startTime=" + startTime + ", endTime=" + endTime + ", breakTime=" + breakTime + ", startComment=" + startComment + ", endComment=" + endComment + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.timeID;
        hash = 47 * hash + Objects.hashCode(this.user);
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
        final WorkTime other = (WorkTime) obj;
        if (!Objects.equals(this.timeID, other.timeID)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

}
