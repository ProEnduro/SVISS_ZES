/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author Martin Six
 */
public class User implements Serializable {

    private int userNr;
    private AccessLevel accessLevel;
    private String PersName;
    //in days
    private int vacationLeft;
    //in minutes
    private int overTimeLeft;
    private String username;
    private String email;
    private LocalDate hiredate;
    private String pass;
    //in hours
    private Double weekTime;
    private boolean disabled = false;

    public User() {
        userNr = -1;
        vacationLeft = 25;
        overTimeLeft = 0;
    }

    public User(User u) {
        this.userNr = u.userNr;
        this.accessLevel = u.accessLevel;
        this.PersName = u.PersName;
        this.vacationLeft = u.vacationLeft;
        this.overTimeLeft = u.overTimeLeft;
        this.username = u.username;
        this.email = u.email;
        this.hiredate = u.hiredate;
        this.pass = u.pass;
        this.weekTime = u.weekTime;
        this.disabled = u.disabled;
    }

    @Deprecated
    public User(int userNr, AccessLevel accessLevel, String PersName, int vacationLeft, int overTimeLeft, String username, String email, LocalDate hiredate, String pass, Double weekTime, boolean disabled) {
        this.userNr = userNr;
        this.accessLevel = accessLevel;
        this.PersName = PersName;
        this.vacationLeft = vacationLeft;
        this.overTimeLeft = overTimeLeft;
        this.username = username;
        this.email = email;
        this.hiredate = hiredate;
        this.pass = pass;
        this.weekTime = weekTime;
        this.disabled = disabled;
    }

    public User(AccessLevel accessLevel, String PersName, int vacationLeft, int overTimeLeft, String username, String email, LocalDate hiredate, String pass, Double weekTime) {
        this();
        this.accessLevel = accessLevel;
        this.PersName = PersName;
        this.vacationLeft = vacationLeft;
        this.overTimeLeft = overTimeLeft;
        this.username = username;
        this.email = email;
        this.hiredate = hiredate;
        this.pass = pass;
        this.weekTime = weekTime;
    }

    public User(AccessLevel accessLevel, String PersName, String username, String email, LocalDate hiredate, String pass, Double weekTime) {
        this();
        this.accessLevel = accessLevel;
        this.PersName = PersName;
        this.username = username;
        this.email = email;
        this.hiredate = hiredate;
        this.pass = pass;
        this.weekTime = weekTime;
    }

    public int getUserNr() {
        return userNr;
    }

    public void setUserNr(int userNr) {
        this.userNr = userNr;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getPersName() {
        return PersName;
    }

    public void setPersName(String PersName) {
        this.PersName = PersName;
    }

    public int getVacationLeft() {
        return vacationLeft;
    }

    public void setVacationLeft(int vacationLeft) {
        this.vacationLeft = vacationLeft;
    }

    public int getOverTimeLeft() {
        return overTimeLeft;
    }

    public void setOverTimeLeft(int overTimeLeft) {
        this.overTimeLeft = overTimeLeft;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getHiredate() {
        return hiredate;
    }

    public void setHiredate(LocalDate hiredate) {
        this.hiredate = hiredate;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Double getWeekTime() {
        return weekTime;
    }

    public void setWeekTime(Double weekTime) {
        this.weekTime = weekTime;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "User{" + "userNr=" + userNr + ", accessLevel=" + accessLevel + ", PersName=" + PersName + ", vacationLeft=" + vacationLeft + ", overTimeLeft=" + overTimeLeft + ", username=" + username + ", email=" + email + ", hiredate=" + hiredate + ", pass=" + pass + ", weekTime=" + weekTime + ", disabled=" + disabled + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.userNr;
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
        final User other = (User) obj;
        if (this.userNr != other.userNr) {
            return false;
        }
        return true;
    }

    public String getDisabledString() {
        if (this.isDisabled()) {
            return "disabled!";
        } else {
            return "enabled";
        }
    }

}
