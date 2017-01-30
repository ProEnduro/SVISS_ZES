/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import at.htlpinkafeld.mobileInterface.service.util.LocalDateAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Martin Six
 */
@XmlRootElement
public class User implements Serializable {

    private Integer userNr;
    private AccessLevel accessLevel;
    private String PersName;
    //in days
    private Integer vacationLeft;
    //in minutes
    private Integer overTimeLeft;
    private String username;
    private String email;
    private LocalDate hiredate;
    private String pass;
    //in hours
    private Double weekTime;
    private boolean disabled = false;

    private List<User> approver;

    public User() {
        userNr = -1;
        vacationLeft = 25;
        overTimeLeft = 0;
        weekTime = 0.0;
    }

    public User(User u) {
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
        if (u.ApproverInitialized()) {
            this.approver = u.getApprover();
        }
    }

    @Deprecated
    public User(Integer userNr, AccessLevel accessLevel, String PersName, Integer vacationLeft, Integer overTimeLeft, String username, String email, LocalDate hiredate, String pass, Double weekTime, Boolean disabled) {
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

    public User(AccessLevel accessLevel, String PersName, Integer vacationLeft, Integer overTimeLeft, String username, String email, LocalDate hiredate, String pass, Double weekTime) {
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

    public Integer getUserNr() {
        return userNr;
    }

    public void setUserNr(Integer userNr) {
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

    public Integer getVacationLeft() {
        return vacationLeft;
    }

    public void setVacationLeft(Integer vacationLeft) {
        this.vacationLeft = vacationLeft;
    }

    public Integer getOverTimeLeft() {
        return overTimeLeft;
    }

    public void setOverTimeLeft(Integer overTimeLeft) {
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

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public LocalDate getHiredate() {
        return hiredate;
    }

    public List<User> getApprover() {
        return approver;
    }

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
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

    public void setApprover(List<User> approver) {
        this.approver = approver;
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
        if (obj instanceof User) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }

            final User other = (User) obj;
            if (this.userNr != other.getUserNr()) {
                return false;
            }
            return true;
        }
        return false;
    }

    public String getDisabledString() {
        if (this.isDisabled()) {
            return "disabled!";
        } else {
            return "enabled";
        }
    }

    public Boolean ApproverInitialized() {
        return approver != null;
    }

}
