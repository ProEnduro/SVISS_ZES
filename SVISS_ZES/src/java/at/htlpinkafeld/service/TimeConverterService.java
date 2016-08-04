/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author Martin Six
 */
public class TimeConverterService {

    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static Date convertLocalDateTimeToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalTime convertDateToLocalTime(Date date) {
        return convertDateToLocalDateTime(date).toLocalTime();
    }

    public static Date convertLocalTimeToDate(LocalDate curDate, LocalTime dateTime) {
        return Date.from((dateTime.atDate(curDate)).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String getCurrentTimeZone() throws ParseException {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

        //Local time zone   
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

        //Time in GMT
        String retVal[] = dateFormatLocal.parse(dateFormatGmt.format(new Date())).toString().split(" ");
        String ret = retVal[4];

        return ret;
    }
}
