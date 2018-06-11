/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SVISS_NES
 */
public class IstZeitEmailReminderService implements Runnable {

    private static final User_DAO USER_DAO;
    private static final SollZeiten_DAO SOLLZEITEN_DAO;

    static {
        USER_DAO = DAOFactory.getDAOFactory().getUserDAO();
        SOLLZEITEN_DAO = DAOFactory.getDAOFactory().getSollZeitenDAO();
    }

    public IstZeitEmailReminderService() {

    }

    @Override
    public void run() {
        Logger.getLogger(IstZeitEmailReminderService.class.getName()).log(Level.INFO, "IstZeitEmailReminder!");

        // User user = USER_DAO.getUserByUsername("NES");
        // List<User> userlist = new ArrayList<>();
        // userlist.add(user);
        
        List<User> userlist = USER_DAO.getList();

        for (User u : userlist) {
            SollZeit sz = SOLLZEITEN_DAO.getSollZeitenByUser_Current(u);

            DayOfWeek firstWorkDay;
            DayOfWeek lastWorkDay;

            firstWorkDay = null;
            if (sz.getSollStartTime(DayOfWeek.MONDAY) != null) {
                firstWorkDay = DayOfWeek.MONDAY;
            } else if (sz.getSollStartTime(DayOfWeek.TUESDAY) != null) {
                firstWorkDay = DayOfWeek.TUESDAY;
            } else if (sz.getSollStartTime(DayOfWeek.WEDNESDAY) != null) {
                firstWorkDay = DayOfWeek.WEDNESDAY;
            } else if (sz.getSollStartTime(DayOfWeek.THURSDAY) != null) {
                firstWorkDay = DayOfWeek.THURSDAY;
            } else if (sz.getSollStartTime(DayOfWeek.FRIDAY) != null) {
                firstWorkDay = DayOfWeek.FRIDAY;
            }

            lastWorkDay = null;
            if (sz.getSollStartTime(DayOfWeek.FRIDAY) != null) {
                lastWorkDay = DayOfWeek.FRIDAY;
            } else if (sz.getSollStartTime(DayOfWeek.THURSDAY) != null) {
                lastWorkDay = DayOfWeek.THURSDAY;
            } else if (sz.getSollStartTime(DayOfWeek.WEDNESDAY) != null) {
                lastWorkDay = DayOfWeek.WEDNESDAY;
            } else if (sz.getSollStartTime(DayOfWeek.TUESDAY) != null) {
                lastWorkDay = DayOfWeek.TUESDAY;
            } else if (sz.getSollStartTime(DayOfWeek.MONDAY) != null) {
                lastWorkDay = DayOfWeek.MONDAY;
            }

            if (firstWorkDay != null && lastWorkDay != null) {
                Calendar firstCal = Calendar.getInstance();
                firstCal.set(GregorianCalendar.DAY_OF_WEEK, firstWorkDay.getValue() + 1);
                firstCal.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, -1);

                Calendar lastCal = Calendar.getInstance();
                lastCal.set(GregorianCalendar.DAY_OF_WEEK, lastWorkDay.getValue() + 1);
                lastCal.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, -1);

                Date firstMidnight = setTimeToMidnight(firstCal.getTime());
                Date lastMidnight = setTimeToMidnight(lastCal.getTime());

                Date now = setTimeToMidnight(new Date());

                if (firstWorkDay.equals(lastWorkDay)) {
                    if (firstMidnight.equals(now)) {
                        EmailService.sendIstZeitReminderEmail(u);
                    }
                } else {
                    if (firstMidnight.equals(now)) {
                        EmailService.sendIstZeitReminderEmail(u);
                    }

                    if (lastMidnight.equals(now)) {
                        EmailService.sendIstZeitReminderEmail(u);
                    }
                }
                // Test to see if the emails are sent!
                // EmailService.sendIstZeitReminderEmail(u);
            }
        }

    }

    public static Date setTimeToMidnight(Date date) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}
