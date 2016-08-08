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
public class UserImpl implements User {

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

    public UserImpl() {
        userNr = -1;
        vacationLeft = 25;
        overTimeLeft = 0;
    }

    public UserImpl(User u) {
        this.userNr = u.getUserNr();
        this.accessLevel = u.getAccessLevel();
        this.PersName = u.getPersName();
        this.vacationLeft = u.getVacationLeft();
        this.overTimeLeft = u.getOverTimeLeft();
        this.username = u.getUsername();
        this.email = u.getEmail();
        this.hiredate = u.getHiredate();
        this.pass = u.getPass();
        this.weekTime = u.getWeekTime();
        this.disabled = u.isDisabled();
    }

    @Deprecated
    public UserImpl(int userNr, AccessLevel accessLevel, String PersName, int vacationLeft, int overTimeLeft, String username, String email, LocalDate hiredate, String pass, Double weekTime, boolean disabled) {
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

    public UserImpl(AccessLevel accessLevel, String PersName, int vacationLeft, int overTimeLeft, String username, String email, LocalDate hiredate, String pass, Double weekTime) {
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

    public UserImpl(AccessLevel accessLevel, String PersName, String username, String email, LocalDate hiredate, String pass, Double weekTime) {
        this();
        this.accessLevel = accessLevel;
        this.PersName = PersName;
        this.username = username;
        this.email = email;
        this.hiredate = hiredate;
        this.pass = pass;
        this.weekTime = weekTime;
    }

    @Override
    public int getUserNr() {
        return userNr;
    }

    @Override
    public void setUserNr(int userNr) {
        this.userNr = userNr;
    }

    @Override
    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    @Override
    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public String getPersName() {
        return PersName;
    }

    @Override
    public void setPersName(String PersName) {
        this.PersName = PersName;
    }

    @Override
    public int getVacationLeft() {
        return vacationLeft;
    }

    @Override
    public void setVacationLeft(int vacationLeft) {
        this.vacationLeft = vacationLeft;
    }

    @Override
    public int getOverTimeLeft() {
        return overTimeLeft;
    }

    @Override
    public void setOverTimeLeft(int overTimeLeft) {
        this.overTimeLeft = overTimeLeft;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public LocalDate getHiredate() {
        return hiredate;
    }

    @Override
    public void setHiredate(LocalDate hiredate) {
        this.hiredate = hiredate;
    }

    @Override
    public String getPass() {
        return pass;
    }

    @Override
    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public Double getWeekTime() {
        return weekTime;
    }

    @Override
    public void setWeekTime(Double weekTime) {
        this.weekTime = weekTime;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
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
        final UserImpl other = (UserImpl) obj;
        if (this.userNr != other.userNr) {
            return false;
        }
        return true;
    }

    @Override
    public String getDisabledString() {
        if (this.isDisabled()) {
            return "disabled!";
        } else {
            return "enabled";
        }
    }

}
