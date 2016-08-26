/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.dao.interf.Holiday_DAO;
import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.dao.interf.WorkTime_DAO;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class OvertimeSynchronisationTask implements Runnable {

    private static final User_DAO USER_DAO;
    private static final WorkTime_DAO WORK_TIME_DAO;
    private static final Absence_DAO ABSENCE_DAO;
    private static final SollZeiten_DAO SOLL_ZEITEN_DAO;
    private static final Holiday_DAO HOLIDAY_DAO;

    static {
        USER_DAO = DAOFactory.getDAOFactory().getUserDAO();
        WORK_TIME_DAO = DAOFactory.getDAOFactory().getWorkTimeDAO();
        ABSENCE_DAO = DAOFactory.getDAOFactory().getAbsenceDAO();
        SOLL_ZEITEN_DAO = DAOFactory.getDAOFactory().getSollZeitenDAO();
        HOLIDAY_DAO = DAOFactory.getDAOFactory().getHolidayDAO();
    }

    private boolean holiday = false;

    @Override
    public void run() {
        List<User> users = USER_DAO.getUserByDisabled(Boolean.FALSE);
        Date endDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.DAY_OF_YEAR, -1);
        Date startDate = c.getTime();
        System.out.println("Overtime");
        holiday = !HOLIDAY_DAO.getHolidayBetweenDates(startDate, endDate).isEmpty();
        if (!holiday) {
            for (User u : users) {
                int overtime = calcOvertime(u, startDate, endDate);
                u.setOverTimeLeft(u.getOverTimeLeft() + overtime);
            }
        }
    }

    // TODO: Fragen wegen Rechtslage bei Überstunden etc.
    public static int calcOvertime(User u, Date startDate, Date endDate) {
        int overtime = 0;

        List<SollZeit> sollZeiten = SOLL_ZEITEN_DAO.getSollZeitenByUser(u);

//        LocalTime lt19Plus = LocalTime.of(19, 0);
//
//        for (WorkTime wt : workTimes) {
//            if (lt19Plus.isBefore(wt.getEndTime().toLocalTime())) {
//                overtime += wt.getStartTime().until(wt.getEndTime().with(lt19Plus), ChronoUnit.MINUTES);
//                overtime += wt.getEndTime().with(lt19Plus).until(wt.getEndTime(), ChronoUnit.MINUTES) * 1.5;
//            } else {
//                overtime += wt.getStartTime().until(wt.getEndTime(), ChronoUnit.MINUTES);
//            }
//            overtime -= wt.getBreakTime();
//            DayOfWeek sDay = wt.getStartTime().getDayOfWeek();
//
//        }
        LocalDate startDateL = TimeConverterService.convertDateToLocalDate(startDate);
        LocalDate endDateL = TimeConverterService.convertDateToLocalDate(endDate);

        for (LocalDate date = startDateL; startDateL.isBefore(endDateL); date = date.plusDays(1)) {
            for (SollZeit sz : sollZeiten) {
                if (sz.getDay().equals(date.getDayOfWeek())) {
                    overtime -= sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
                }
            }
        }
//        overtime -= u.getWeekTime() * 60;
//        List<Absence> absences = ABSENCE_DAO.getAbsencesByUser_BetweenDates(u, startDate, endDate);
//
//        for (Absence a : absences) {
//            if (a.getStartTime().isBefore(TimeConverterService.convertDateToLocalDateTime(startDate))) {
//                a.setStartTime(TimeConverterService.convertDateToLocalDateTime(startDate));
//            }
//            if (a.getEndTime().isAfter(TimeConverterService.convertDateToLocalDateTime(endDate))) {
//                a.setEndTime(TimeConverterService.convertDateToLocalDateTime(endDate).minusMinutes(1));
//            }
//            switch (a.getAbsenceType().getAbsenceName()) {
//                case "holiday":
//                    if (!a.isAcknowledged()) {
//                        break;
//                    }
//                    int holidayLength = (a.getEndTime().getDayOfYear() - a.getStartTime().getDayOfYear() + 1);
//                    DayOfWeek hDay = a.getStartTime().getDayOfWeek();
//                    for (int i = 0; i < holidayLength; i++, hDay.plus(1)) {
//                        for (SollZeit sz : sollZeiten) {
//                            if (sz.getDay().equals(hDay)) {
//                                int diff = (int) sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
//                                if (diff > 6 * 60) {
//                                    diff -= 30;
//                                }
//                                overtime += diff;
//                            }
//                        }
//                    }
//                    break;
//                case "time compensation":
//                    if (!a.isAcknowledged()) {
//                        break;
//                    }
//                case "medical leave":
//                    int dayNum = a.getEndTime().getDayOfYear() - a.getStartTime().getDayOfYear() + 1;
//                    DayOfWeek sDay = a.getStartTime().getDayOfWeek();
//                    for (int i = 0; i < dayNum; i++, sDay.plus(1)) {
//                        for (SollZeit sz : sollZeiten) {
//                            if (sz.getDay().equals(sDay)) {
//                                int diff = 0;
//                                if (i == 0 || i == (dayNum - 1)) {
//                                    if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime()) && a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime())) {
//                                        diff = (int) a.getStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
//                                    } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollStartTime()) || a.getStartTime().toLocalTime().isAfter(sz.getSollEndTime())) {
//                                    } else if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime())) {
//                                        diff = (int) a.getStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
//                                    } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime())) {
//                                        diff = (int) sz.getSollStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
//                                    } else {
//                                        diff = (int) sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
//                                    }
//                                } else {
//                                    diff = (int) sz.getSollStartTime().until(sz.getSollEndTime(), ChronoUnit.MINUTES);
//                                }
//                                if (diff > 6 * 60) {
//                                    diff -= 30;
//                                }
//                                overtime += diff;
//                            }
//                        }
//                    }
//                    break;
//                case "business-related absence":
//                    break;
//            }
//        }
        return overtime;
    }
}
