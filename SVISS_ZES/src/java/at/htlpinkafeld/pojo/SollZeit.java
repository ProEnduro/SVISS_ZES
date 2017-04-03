/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import at.htlpinkafeld.mobileInterface.service.util.DayOfWeek_LocalTimeMapAdapter;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Martin Six
 */
@XmlRootElement
public class SollZeit implements Serializable {

    private static final long serialVersionUID = 131355900018761859L;

    private Integer sollZeitID;
    private User user;
    private Map<DayOfWeek, LocalTime> sollStartTimeMap;
    private Map<DayOfWeek, LocalTime> sollEndTimeMap;
    private LocalDateTime validFrom;

    @Deprecated
    public SollZeit() {
    }

    public SollZeit(SollZeit sz) {
        this.user = new UserProxy(sz.user);
        this.sollStartTimeMap = sz.sollStartTimeMap;
        this.sollEndTimeMap = sz.sollEndTimeMap;
    }

    @Deprecated
    public SollZeit(Integer sollZeitID, User user, Map<DayOfWeek, LocalTime> sollStartTimeMap, Map<DayOfWeek, LocalTime> sollEndTimeMap) {
        this.sollZeitID = sollZeitID;
        this.user = user;
        this.sollStartTimeMap = sollStartTimeMap;
        this.sollEndTimeMap = sollEndTimeMap;
    }

    public SollZeit(User user, Map<DayOfWeek, LocalTime> sollStartTimeMap, Map<DayOfWeek, LocalTime> sollEndTimeMap) {
        this.user = user;
        this.sollStartTimeMap = sollStartTimeMap;
        this.sollEndTimeMap = sollEndTimeMap;
    }

    public static DayOfWeek getDayOfWeekFromDBShort(String s) {
        if (s == null) {
            return null;
        }
        switch (s.toUpperCase(Locale.GERMAN)) {
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

    public Integer getSollZeitID() {
        return sollZeitID;
    }

    public void setSollZeitID(Integer sollZeitID) {
        this.sollZeitID = sollZeitID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    @XmlJavaTypeAdapter(DayOfWeek_LocalTimeMapAdapter.class)
    public Map<DayOfWeek, LocalTime> getSollStartTimeMap() {
        return sollStartTimeMap;
    }

    @XmlJavaTypeAdapter(DayOfWeek_LocalTimeMapAdapter.class)
    public void setSollStartTimeMap(Map<DayOfWeek, LocalTime> sollStartTimeMap) {
        this.sollStartTimeMap = sollStartTimeMap;
    }

    @XmlJavaTypeAdapter(DayOfWeek_LocalTimeMapAdapter.class)
    public Map<DayOfWeek, LocalTime> getSollEndTimeMap() {
        return sollEndTimeMap;
    }

    @XmlJavaTypeAdapter(DayOfWeek_LocalTimeMapAdapter.class)
    public void setSollEndTimeMap(Map<DayOfWeek, LocalTime> sollEndTimeMap) {
        this.sollEndTimeMap = sollEndTimeMap;
    }

    public LocalTime getSollStartTime(DayOfWeek dow) {
        return sollStartTimeMap.get(dow);
    }

    public LocalTime getSollEndTime(DayOfWeek dow) {
        return sollEndTimeMap.get(dow);
    }

    public long getSollTimeInHour(DayOfWeek dow) {
        return sollStartTimeMap.get(dow).until(sollEndTimeMap.get(dow), ChronoUnit.HOURS);
    }

    @Override
    public String toString() {
        return "SollZeit{" + "sollZeitID=" + sollZeitID + ", user=" + user + ", sollStartTimeMap=" + sollStartTimeMap + ", sollEndTimeMap=" + sollEndTimeMap + ", validFrom=" + validFrom + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.validFrom);
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
        if (!Objects.equals(this.sollZeitID, other.sollZeitID)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

}
