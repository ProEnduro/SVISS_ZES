/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import at.htlpinkafeld.mobileInterface.service.util.LocalDateTimeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Martin Six
 */
@XmlRootElement
public class UserHistoryEntry implements Serializable {

    private LocalDateTime timestamp;
    private User user;
    private Integer overtime;
    private Integer vacation;

    public UserHistoryEntry() {
    }

    public UserHistoryEntry(UserHistoryEntry entry) {
        this.timestamp = entry.timestamp;
        this.user = new UserProxy(entry.user);
        this.overtime = entry.overtime;
        this.vacation = entry.vacation;
    }

    public UserHistoryEntry(LocalDateTime timestamp, User user, int overtime, int vacation) {
        this.timestamp = timestamp;
        this.user = user;
        this.overtime = overtime;
        this.vacation = vacation;
    }

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getOvertime() {
        return overtime;
    }

    public void setOvertime(Integer overtime) {
        this.overtime = overtime;
    }

    public Integer getVacation() {
        return vacation;
    }

    public void setVacation(Integer vacation) {
        this.vacation = vacation;
    }

    @Override
    public String toString() {
        return "UserHistoryEntry{" + "timestamp=" + timestamp + ", user=" + user + ", overtime=" + overtime + ", vacation=" + vacation + '}';
    }

}
