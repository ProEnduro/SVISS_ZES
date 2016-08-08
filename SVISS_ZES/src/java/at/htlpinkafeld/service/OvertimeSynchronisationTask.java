/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.dao.interf.WorkTime_DAO;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.SollZeiten;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class OvertimeSynchronisationTask implements Runnable {

    private static final User_DAO USER__DAO;
    private static final WorkTime_DAO WORK_TIME__DAO;
    private static final Absence_DAO ABSENCE__DAO;
    private static final SollZeiten_DAO SOLL_ZEITEN__DAO;

    static {
        USER__DAO = DAOFactory.getDAOFactory().getUserDAO();
        WORK_TIME__DAO = DAOFactory.getDAOFactory().getWorkTimeDAO();
        ABSENCE__DAO = DAOFactory.getDAOFactory().getAbsenceDAO();
        SOLL_ZEITEN__DAO = DAOFactory.getDAOFactory().getSollZeitenDAO();
    }

    @Override
    public void run() {
        List<User> users = USER__DAO.getUserByDisabled(Boolean.FALSE);
        Date endDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.DAY_OF_MONTH, -1);
        Date startDate = c.getTime();
        System.out.println("Overtime");
        for (User u : users) {
            int overtime = calcOvertime(u, startDate, endDate);
            u.setOverTimeLeft(u.getOverTimeLeft() + overtime);
        }
    }

    // TODO: Fragen wegen Rechtslage bei Überstunden etc.
    public static int calcOvertime(User u, Date startDate, Date endDate) {
        int overtime = 0;
        List<WorkTime> workTimes = WORK_TIME__DAO.getWorkTimesFromUserBetweenDates(u, startDate, endDate);
        List<SollZeiten> sollZeiten = SOLL_ZEITEN__DAO.getSollZeitenByUser(u);
        for (WorkTime wt : workTimes) {
            overtime += calcDiffInMin(wt.getStartTime().toLocalTime(), wt.getEndTime().toLocalTime());
            overtime -= wt.getBreakTime();
            DayOfWeek sDay = wt.getStartTime().getDayOfWeek();
            for (SollZeiten sz : sollZeiten) {
                if (sz.getDay().equals(sDay)) {
                    overtime -= calcDiffInMin(sz.getSollStartTime(), sz.getSollEndTime());
                }
            }
        }

//        overtime -= u.getWeekTime() * 60;

        List<Absence> absences = ABSENCE__DAO.getAbsencesByUserBetweenDates(u, startDate, endDate);

        for (Absence a : absences) {
            if (a.getStartTime().isBefore(TimeConverterService.convertDateToLocalDateTime(startDate))) {
                a.setStartTime(TimeConverterService.convertDateToLocalDateTime(startDate));
            }
            if (a.getEndTime().isAfter(TimeConverterService.convertDateToLocalDateTime(endDate))) {
                a.setEndTime(TimeConverterService.convertDateToLocalDateTime(endDate).minusMinutes(1));
            }
            switch (a.getAbsenceType().getAbsenceName()) {
                case "holiday":
                    int holidayLength = (a.getEndTime().getDayOfYear() - a.getStartTime().getDayOfYear() + 1);
                    DayOfWeek hDay = a.getStartTime().getDayOfWeek();
                    for (int i = 0; i < holidayLength; i++, hDay.plus(1)) {
                        for (SollZeiten sz : sollZeiten) {
                            if (sz.getDay().equals(hDay)) {
                                int diff = calcDiffInMin(sz.getSollStartTime(), sz.getSollEndTime());
                                if (diff > 6 * 60) {
                                    diff -= 30;
                                }
                                overtime += diff;
                            }
                        }
                    }
                    break;
                case "medical leave":
                case "time compensation":
                    int dayNum = a.getEndTime().getDayOfYear() - a.getStartTime().getDayOfYear() + 1;
                    DayOfWeek sDay = a.getStartTime().getDayOfWeek();
                    for (int i = 0; i < dayNum; i++, sDay.plus(1)) {
                        for (SollZeiten sz : sollZeiten) {
                            if (sz.getDay().equals(sDay)) {
                                int diff;
                                if (i == 0 || i == (dayNum - 1)) {
                                    if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime()) && a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime())) {
                                        diff = calcDiffInMin(a.getStartTime().toLocalTime(), a.getEndTime().toLocalTime());
                                    } else if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime())) {
                                        diff = calcDiffInMin(a.getStartTime().toLocalTime(), sz.getSollEndTime());
                                    } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime())) {
                                        diff = calcDiffInMin(sz.getSollStartTime(), a.getEndTime().toLocalTime());
                                    } else {
                                        diff = calcDiffInMin(sz.getSollStartTime(), sz.getSollEndTime());
                                    }
                                } else {
                                    diff = calcDiffInMin(sz.getSollStartTime(), sz.getSollEndTime());
                                }
                                if (diff > 6 * 60) {
                                    diff -= 30;
                                }
                                overtime += diff;
                            }
                        }
                    }
                    break;
                case "business-related absence":
                    break;
            }
        }
        return overtime;
    }

    private static int calcDiffInMin(LocalTime startTime, LocalTime endTime) {
        int diff = (endTime.getHour() - startTime.getHour()) * 60;
        diff += endTime.getMinute() - startTime.getMinute();
        return diff;
    }
}
