/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.AbsenceType_DAO;
import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.dao.interf.AccessLevel_DAO;
import at.htlpinkafeld.dao.interf.Holiday_DAO;
import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.dao.interf.WorkTime_DAO;

/**
 *
 * @author Martin Six
 */
public class InMemoryDAOFactory extends DAOFactory {

    private final User_DAO user_DAO;
    private final AccessLevel_DAO accessLevel_DAO;
    private final WorkTime_DAO workTime_DAO;
    private final SollZeiten_DAO sollZeiten_DAO;
    private final AbsenceType_DAO absenceType_DAO;
    private final Absence_DAO absence_DAO;
    private final Holiday_DAO holiday_DAO;

    public InMemoryDAOFactory() {
        user_DAO = new User_InMemoryDAO();
        accessLevel_DAO = new AccessLevel_InMemoryDAO();
        workTime_DAO = new WorkTime_InMemoryDAO();
        sollZeiten_DAO = new SollZeiten_InMemoryDAO();
        absenceType_DAO = new AbsenceType_InMemoryDAO();
        absence_DAO = new Absence_InMemoryDAO();
        holiday_DAO = new Holiday_InMemoryDAO();
    }

    @Override
    public synchronized User_DAO getUserDAO() {
        return user_DAO;
    }

    @Override
    public synchronized AccessLevel_DAO getAccessLevelDAO() {
        return accessLevel_DAO;
    }

    @Override
    public synchronized WorkTime_DAO getWorkTimeDAO() {
        return workTime_DAO;
    }

    @Override
    public SollZeiten_DAO getSollZeitenDAO() {
        return sollZeiten_DAO;
    }

    @Override
    public synchronized AbsenceType_DAO getAbsenceTypeDAO() {
        return absenceType_DAO;
    }

    @Override
    public synchronized Absence_DAO getAbsenceDAO() {
        return absence_DAO;
    }

    @Override
    public Holiday_DAO getHolidayDAO() {
        return holiday_DAO;
    }

}
