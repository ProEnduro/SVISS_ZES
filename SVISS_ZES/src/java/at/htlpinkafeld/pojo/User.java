/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import java.util.Date;

/**
 *
 * @author Martin Six
 */
public class User {

    private final int persNr;
    private AccessLevel accessLevel;
    private String PersName;
    private int vacationLeft;
    private int overTimeLeft;
    private String username;
    private String email;
    private Date hiredate;
    private String pass;
    private Double weekTime;

    public User(int persnr, AccessLevel accessLevel, String PersName, int vacationLeft, int overTimeLeft, String username, String email, Date hiredate, String pass, Double weekTime) {
        this.persNr = persnr;
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

    public User(int persnr, AccessLevel accessLevel, String PersName, String username, String email, Date hiredate, String pass, Double weekTime) {
        this.persNr = persnr;
        this.accessLevel = accessLevel;
        this.PersName = PersName;
        this.username = username;
        this.email = email;
        this.hiredate = hiredate;
        this.pass = pass;
        this.weekTime = weekTime;
    }

    public int getPersNr() {
        return persNr;
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

    public Date getHiredate() {
        return hiredate;
    }

    public void setHiredate(Date hiredate) {
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

    @Override
    public String toString() {
        return "User{" + "persnr=" + persNr + ", accessLevelId=" + accessLevel + ", PersName=" + PersName + ", vacationLeft=" + vacationLeft + ", overTimeLeft=" + overTimeLeft + ", username=" + username + ", email=" + email + ", hiredate=" + hiredate + ", pass=" + pass + ", weekTime=" + weekTime + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.persNr;
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
        if (this.persNr != other.persNr) {
            return false;
        }
        return true;
    }
    
}
