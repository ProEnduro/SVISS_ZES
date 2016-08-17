/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.WorkTime_DAO;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ÐarkHell2
 */
public class IstZeitService {

    static WorkTime_DAO workDAO;
    static WorkTime workT;
    static WorkTime test;

    static {
        workDAO = DAOFactory.getDAOFactory().getWorkTimeDAO();
    }

    public static void addIstTime(WorkTime t) {
        workDAO.insert(t);
    }

    public static List<WorkTime> getWorktimeByUser(User u) {
        return workDAO.getWorkTimesByUser(u);
    }

    public static List<WorkTime> getAllWorkTime() {
        return workDAO.getList();
    }

    public static void update(WorkTime o) {
        workDAO.update(o);
    }

    public static void delete(WorkTime o) {
        workDAO.delete(o);
    }

    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static Date convertLocalDateTimeToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static boolean breakTimeBooleanCalc(Date startDate, Date endDate) {

        LocalDateTime start = IstZeitService.convertDateToLocalDateTime(startDate);
        LocalDateTime end = IstZeitService.convertDateToLocalDateTime(endDate);

        double dif = start.until(end, ChronoUnit.HOURS);

        if (dif >= 6.00) {
            return true;
        } else {
            return false;
        }
    }
    
    public static List<WorkTime> getWorkTimeForUserBetweenStartAndEndDate(User u, Date start, Date end){
        return workDAO.getWorkTimesFromUserBetweenDates(u, start, end);
    }
}
