/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.dao.interf.WorkTime_DAO;
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

    private final User_DAO user_DAO;
    private final WorkTime_DAO workTime_DAO;
    private final Absence_DAO absence_DAO;

    public OvertimeSynchronisationTask() {
        user_DAO = DAOFactory.getDAOFactory().getUserDAO();
        workTime_DAO = DAOFactory.getDAOFactory().getWorkTimeDAO();
        absence_DAO = DAOFactory.getDAOFactory().getAbsenceDAO();
    }

    @Override
    public void run() {
        List<User> users = user_DAO.getUserByDisabled(Boolean.FALSE);
        Date endDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.DAY_OF_MONTH, -7);
        Date startDate = c.getTime();
        System.out.println("noplan");
        for (User u : users) {
            List<WorkTime> workTimes = workTime_DAO.getWorkTimesFromUserBetweenDates(u, startDate, endDate);
            
        }
    }

}
