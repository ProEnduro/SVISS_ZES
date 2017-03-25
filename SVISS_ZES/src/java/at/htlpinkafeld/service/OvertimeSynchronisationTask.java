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
import at.htlpinkafeld.pojo.AbsenceTypeNew;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import java.time.DayOfWeek;
import java.time.LocalDate;
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

    private boolean noHoliday = true;

    @Override
    public void run() {
        List<User> users = USER_DAO.getUserByDisabled(Boolean.FALSE);
        Date nextDate = TimeConverterService.convertLocalDateTimeToDate(LocalDate.now().atStartOfDay());
        Calendar c = Calendar.getInstance();
        c.setTime(nextDate);
        c.add(Calendar.DAY_OF_YEAR, -1);
        Date startDate = c.getTime();
        noHoliday = HOLIDAY_DAO.getHolidayBetweenDates(startDate, nextDate).isEmpty();
        if (noHoliday) {
            users.forEach((u) -> {
                addDefaultTimeAndUpdateOvertime(u, startDate, nextDate);
            });
        }
    }

    //endDate has to be the start of the next day
    public static void addDefaultTimeAndUpdateOvertime(User u, Date startDate, Date nextDate) {
        LocalDate startLd = TimeConverterService.convertDateToLocalDate(startDate);
        DayOfWeek currentDay = startLd.getDayOfWeek();
        SollZeit sz = SOLL_ZEITEN_DAO.getSollZeitenByUser_Current(u);
        List<WorkTime> workTimes = WORK_TIME_DAO.getWorkTimesFromUserBetweenDates(u, startDate, nextDate);
        List<Absence> absences = ABSENCE_DAO.getAbsencesByUser_BetweenDates(u, startDate, nextDate);
        if (workTimes.isEmpty() && sz != null) {
            if (absences.isEmpty()) {
                int breaktime = 0;
                if (sz.getSollTimeInHour(currentDay) >= 6) {
                    breaktime = 30;
                }
                WorkTime wt = new WorkTime(u, sz.getSollStartTime(currentDay).atDate(startLd), sz.getSollEndTime(currentDay).atDate(startLd), breaktime, "", "");
                IstZeitService.addIstTime(wt);
            } else {
                int diff = 0;
                for (Absence a : absences) {
                    if (a.getAbsenceType().equals(AbsenceTypeNew.MEDICAL_LEAVE) || a.getAbsenceType().equals(AbsenceTypeNew.TIME_COMPENSATION)) {
                        if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime(currentDay)) && a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime(currentDay))) {
                            diff += (int) a.getStartTime().until(a.getEndTime(), ChronoUnit.MINUTES);
                        } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollStartTime(currentDay)) || a.getStartTime().toLocalTime().isAfter(sz.getSollEndTime(currentDay))) {
                        } else if (a.getStartTime().toLocalTime().isAfter(sz.getSollStartTime(currentDay))) {
                            diff += (int) a.getStartTime().until(sz.getSollEndTime(currentDay), ChronoUnit.MINUTES);
                        } else if (a.getEndTime().toLocalTime().isBefore(sz.getSollEndTime(currentDay))) {
                            diff += (int) sz.getSollStartTime(currentDay).until(a.getEndTime(), ChronoUnit.MINUTES);
                        } else {
                            diff += (int) sz.getSollStartTime(currentDay).until(sz.getSollEndTime(currentDay), ChronoUnit.MINUTES);
                        }
                    }
                }
                diff -= sz.getSollStartTime(currentDay).until(sz.getSollEndTime(currentDay), ChronoUnit.MINUTES);
                u.setOverTimeLeft(u.getOverTimeLeft() + diff);
                USER_DAO.update(u);
            }
        }
    }
}
