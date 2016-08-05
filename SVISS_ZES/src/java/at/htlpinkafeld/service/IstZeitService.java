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

    public IstZeitService() {
    }

    public static void addIstTime(User user, LocalDateTime startTime, LocalDateTime endTime) {
        workT = new WorkTime(user, startTime, endTime, 0, "", "");
        workDAO = DAOFactory.getDAOFactory().getWorkTimeDAO();
        workDAO.insert(workT);
    }

    public static void addIstTime(WorkTime t) {
        workDAO = DAOFactory.getDAOFactory().getWorkTimeDAO();
        workDAO.insert(t);
    }

    public static List<WorkTime> getWorktimeByUser(User u) {
        workDAO = DAOFactory.getDAOFactory().getWorkTimeDAO();
        return workDAO.getWorkTimesByUser(u);
    }

    public static List<WorkTime> getAllWorkTime() {
        workDAO = DAOFactory.getDAOFactory().getWorkTimeDAO();
        return workDAO.getList();
    }

    public static void update(WorkTime o) {
        workDAO = DAOFactory.getDAOFactory().getWorkTimeDAO();
        workDAO.update(o);
    }

    public static void delete(WorkTime o) {
        workDAO = DAOFactory.getDAOFactory().getWorkTimeDAO();
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

        double sd = start.getDayOfYear() * 24;
        double sh = start.getHour();
        double sm = start.getMinute() / 60;
        double st = sd + sh + sm;
        

        double ed = end.getDayOfYear() * 24;
        double eh = end.getHour();
        double em = end.getMinute() / 60;
        double et = ed + eh + em;
        

        double dif = et - st;
        

        if (dif >= 6.00) {
            return true;
        } else {
            return false;
        }
    }
}
