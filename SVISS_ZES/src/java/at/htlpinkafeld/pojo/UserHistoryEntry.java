/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import at.htlpinkafeld.mobileInterface.service.util.LocalDateTimeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Martin Six
 */
@XmlRootElement
public class UserHistoryEntry implements Serializable {

    private static final long serialVersionUID = 1903009026286137080L;

    private LocalDateTime timestamp;
    private User user;
    //in Minuten
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

    public String getTimestampAsYearMonthString() {
        return this.timestamp.format(DateTimeFormatter.ofPattern("yyyy MMMM"));
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
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.timestamp);
        hash = 17 * hash + Objects.hashCode(this.user);
        hash = 17 * hash + Objects.hashCode(this.overtime);
        hash = 17 * hash + Objects.hashCode(this.vacation);
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
        final UserHistoryEntry other = (UserHistoryEntry) obj;
        if (!Objects.equals(this.timestamp.getYear(), other.timestamp.getYear())) {
            return false;
        }
        if (!Objects.equals(this.timestamp.getMonthValue(), other.timestamp.getMonthValue())) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UserHistoryEntry{" + "timestamp=" + timestamp + ", user=" + user + ", overtime=" + overtime + ", vacation=" + vacation + '}';
    }

}
