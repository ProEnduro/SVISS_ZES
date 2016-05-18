/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import com.sun.security.auth.SolarisNumericGroupPrincipal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

/**
 *
 * @author Martin Six
 */
public class SollZeiten {

    private DayOfWeek day;
    private User user;
    private LocalTime sollStartTime;
    private LocalTime sollEndTime;

    public SollZeiten(SollZeiten sz) {
        this.day = sz.day;
        this.user = sz.user;
        this.sollStartTime = sz.sollStartTime;
        this.sollEndTime = sz.sollEndTime;
    }

    public SollZeiten(DayOfWeek day, User user, LocalTime sollStartTime, LocalTime sollEndTime) {
        this.day = day;
        this.user = user;
        this.sollStartTime = sollStartTime;
        this.sollEndTime = sollEndTime;
    }

    public static DayOfWeek getDayOfWeekFromDBShort(String s) {
        if (s == null) {
            return null;
        }
        switch (s.toUpperCase()) {
            case "MON":
                return DayOfWeek.MONDAY;
            case "TUE":
                return DayOfWeek.TUESDAY;
            case "WED":
                return DayOfWeek.WEDNESDAY;
            case "THU":
                return DayOfWeek.THURSDAY;
            case "FRI":
                return DayOfWeek.FRIDAY;
            case "SAT":
                return DayOfWeek.SATURDAY;
            case "SUN":
                return DayOfWeek.SUNDAY;
        }
        return null;
    }

    public static String getDBShortFromDayOfWeek(DayOfWeek dow) {
        if (dow != null) {
            return dow.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.ENGLISH);
        }
        return null;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalTime getSollStartTime() {
        return sollStartTime;
    }

    public void setSollStartTime(LocalTime sollStartTime) {
        this.sollStartTime = sollStartTime;
    }

    public LocalTime getSollEndTime() {
        return sollEndTime;
    }

    public void setSollEndTime(LocalTime sollEndTime) {
        this.sollEndTime = sollEndTime;
    }

    @Override
    public String toString() {
        return "SollZeiten{" + "day=" + day + ", user=" + user + ", sollStartTime=" + sollStartTime + ", sollEndTime=" + sollEndTime + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.day);
        hash = 67 * hash + Objects.hashCode(this.user);
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
        final SollZeiten other = (SollZeiten) obj;
        if (this.day != other.day) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

}
