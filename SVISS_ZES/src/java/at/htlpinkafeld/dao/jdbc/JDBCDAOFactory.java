/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

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
public class JDBCDAOFactory extends DAOFactory {

    private final User_DAO user_DAO;
    private final AccessLevel_DAO accessLevel_DAO;
    private final WorkTime_DAO workTime_DAO;
    private final SollZeiten_DAO sollZeiten_DAO;
    private final AbsenceType_DAO absenceType_DAO;
    private final Absence_DAO absence_DAO;
    private final Holiday_DAO holiday_DAO;

    public JDBCDAOFactory() {
        user_DAO = new User_JDBCDAO();
        accessLevel_DAO = new AccessLevel_JDBCDAO();
        workTime_DAO = new WorkTime_JDBCDAO();
        sollZeiten_DAO = new SollZeiten_JDBCDAO();
        absenceType_DAO = new AbsenceType_JDBCDAO();
        absence_DAO = new Absence_JDBCDAO();
        holiday_DAO = new Holiday_JDBCDAO();
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
    public synchronized SollZeiten_DAO getSollZeitenDAO() {
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
