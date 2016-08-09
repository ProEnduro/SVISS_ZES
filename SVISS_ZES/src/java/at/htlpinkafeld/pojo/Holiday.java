/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import java.time.LocalDate;

/**
 *
 * @author Martin Six
 */
public class Holiday {

    private LocalDate holidayDate;
    private String holidayComment;

    public Holiday(Holiday h) {
        this.holidayDate = h.holidayDate;
        this.holidayComment = h.holidayComment;
    }

    public Holiday(LocalDate holidayDate, String holidayComment) {
        this.holidayDate = holidayDate;
        this.holidayComment = holidayComment;
    }

    public LocalDate getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(LocalDate holidayDate) {
        this.holidayDate = holidayDate;
    }

    public String getHolidayComment() {
        return holidayComment;
    }

    public void setHolidayComment(String holidayComment) {
        this.holidayComment = holidayComment;
    }

}
