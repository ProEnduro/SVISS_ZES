/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Martin Six
 */
public class WorkTime {

    private int timeID;
    private User user;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    //Break Time in minutes
    private int breakTime;
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
    }

    @Deprecated
    public WorkTime(int timeID, User user, LocalDateTime startTime, LocalDateTime endTime, int breakTime, String startComment, String endComment) {
        this.timeID = timeID;
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.breakTime = breakTime;
        this.startComment = startComment;
        this.endComment = endComment;
    }

    public WorkTime(User user, LocalDateTime startTime, LocalDateTime endTime, int breakTime, String startComment, String endComment) {
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

    public int getTimeID() {
        return timeID;
    }

    public void setTimeID(int timeID) {
        this.timeID = timeID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(int breakTime) {
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
        if (this.timeID != other.timeID) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

}
