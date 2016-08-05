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
import at.htlpinkafeld.pojo.AbsenceType;
import at.htlpinkafeld.pojo.SollZeiten;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
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
    private static final List<AbsenceType> ABSENCE_TYPES;

    static {
        USER__DAO = DAOFactory.getDAOFactory().getUserDAO();
        WORK_TIME__DAO = DAOFactory.getDAOFactory().getWorkTimeDAO();
        ABSENCE__DAO = DAOFactory.getDAOFactory().getAbsenceDAO();
        SOLL_ZEITEN__DAO = DAOFactory.getDAOFactory().getSollZeitenDAO();
        ABSENCE_TYPES = DAOFactory.getDAOFactory().getAbsenceTypeDAO().getList();
    }

    @Override
    public void run() {
        List<User> users = USER__DAO.getUserByDisabled(Boolean.FALSE);
        Date endDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.DAY_OF_MONTH, -7);
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
        for (WorkTime wt : workTimes) {
            overtime += wt.getEndTime().getHour() - wt.getStartTime().getHour() * 60.0;
            overtime += (wt.getEndTime().getMinute() - wt.getStartTime().getMinute());
            overtime -= wt.getBreakTime();
        }
        overtime -= u.getWeekTime() * 60;

        List<SollZeiten> sollZeiten = SOLL_ZEITEN__DAO.getSollZeitenByUser(u);

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
                    overtime += (a.getEndTime().getDayOfYear() - a.getStartTime().getDayOfYear() + 1) * 24 * 60;
                    break;
                case "time compensation":
                    overtime -= (a.getEndTime().getDayOfYear() - a.getStartTime().getDayOfYear()) * 24 * 60;
                    overtime -= (a.getEndTime().getHour() - a.getStartTime().getHour()) * 60;
                    overtime -= a.getEndTime().getMinute() - a.getStartTime().getMinute();
                case "medical leave":
                    break;
                case "business-related absence":
                    break;
            }
        }
        return overtime;
    }
}
