/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import at.htlpinkafeld.mobileInterface.service.util.LocalDateAdapter;
import java.time.LocalDate;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Martin Six
 */
@XmlRootElement
public class Holiday {

    private LocalDate holidayDate;
    private String holidayComment;

    @Deprecated
    public Holiday() {
    }

    public Holiday(Holiday h) {
        this.holidayDate = h.holidayDate;
        this.holidayComment = h.holidayComment;
    }

    public Holiday(LocalDate holidayDate, String holidayComment) {
        this.holidayDate = holidayDate;
        this.holidayComment = holidayComment;
    }

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public LocalDate getHolidayDate() {
        return holidayDate;
    }

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public void setHolidayDate(LocalDate holidayDate) {
        this.holidayDate = holidayDate;
    }

    public String getHolidayComment() {
        return holidayComment;
    }

    public void setHolidayComment(String holidayComment) {
        this.holidayComment = holidayComment;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.holidayDate);
        hash = 37 * hash + Objects.hashCode(this.holidayComment);
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
        final Holiday other = (Holiday) obj;
        if (!Objects.equals(this.holidayDate, other.holidayDate)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Holiday{" + "holidayDate=" + holidayDate + ", holidayComment=" + holidayComment + '}';
    }

}
