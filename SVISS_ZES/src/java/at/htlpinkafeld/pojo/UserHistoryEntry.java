/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import java.time.LocalDateTime;

/**
 *
 * @author Martin Six
 */
public class UserHistoryEntry {

    private LocalDateTime timestamp;
    private User user;
    private int overtime;
    private int vacation;

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getOvertime() {
        return overtime;
    }

    public void setOvertime(int overtime) {
        this.overtime = overtime;
    }

    public int getVacation() {
        return vacation;
    }

    public void setVacation(int vacation) {
        this.vacation = vacation;
    }

    @Override
    public String toString() {
        return "UserHistoryEntry{" + "timestamp=" + timestamp + ", user=" + user + ", overtime=" + overtime + ", vacation=" + vacation + '}';
    }

}
