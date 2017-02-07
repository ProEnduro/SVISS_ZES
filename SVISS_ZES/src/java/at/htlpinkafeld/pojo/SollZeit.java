/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import at.htlpinkafeld.mobileInterface.service.util.DayOfWeekAdapter;
import at.htlpinkafeld.mobileInterface.service.util.LocalTimeAdapter;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Martin Six
 */
@XmlRootElement
public class SollZeit implements Serializable {

    private DayOfWeek day;
    private User user;
    private LocalTime sollStartTime;
    private LocalTime sollEndTime;

    @Deprecated
    public SollZeit() {
    }

    public SollZeit(SollZeit sz) {
        this.day = sz.day;
        this.user = new UserProxy(sz.user);
        this.sollStartTime = sz.sollStartTime;
        this.sollEndTime = sz.sollEndTime;
    }

    public SollZeit(DayOfWeek day, User user, LocalTime sollStartTime, LocalTime sollEndTime) {
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

    @XmlJavaTypeAdapter(DayOfWeekAdapter.class)
    public DayOfWeek getDay() {
        return day;
    }

    @XmlJavaTypeAdapter(DayOfWeekAdapter.class)
    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        final SollZeit other = (SollZeit) obj;
        if (this.day != other.day) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

}
