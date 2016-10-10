/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.factory;

import at.htlpinkafeld.dao.dummy.InMemoryDAOFactory;
import at.htlpinkafeld.dao.interf.AbsenceType_DAO;
import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.dao.interf.AccessLevel_DAO;
import at.htlpinkafeld.dao.interf.Holiday_DAO;
import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.dao.interf.UserHistory_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.dao.interf.WorkTime_DAO;
import at.htlpinkafeld.dao.jdbc.JDBCDAOFactory;
import at.htlpinkafeld.dao.util.InvalidDAOFactoryTypeException;

/**
 *
 * @author Martin Six
 */
public abstract class DAOFactory {

    public static final int JDBCDAO = 0;
    public static final int DUMMY_DAO = 1;
    private static int daoType;

    private static DAOFactory daof;

    public static synchronized DAOFactory getDAOFactory() {
        if (daof == null) {
            daoType = JDBCDAO;
            switch (daoType) {
                case JDBCDAO:
                    daof = new JDBCDAOFactory();
                    break;
                case DUMMY_DAO:
                    daof = new InMemoryDAOFactory();
                    break;
                default:
                    daof = null;
            }
        }
        return daof;
    }

    /**
     * Set and recreate DAOFactory. only constants JDBCDAO and DUMMY_DAO should
     * be used
     *
     * @param daoType
     * @throws InvalidDAOFactoryTypeException
     */
    public static synchronized void setDAOFactoryType(int daoType) throws InvalidDAOFactoryTypeException {
        if (daoType == JDBCDAO || daoType == DUMMY_DAO) {
            DAOFactory.daoType = daoType;
            switch (daoType) {
                case JDBCDAO:
                    daof = new JDBCDAOFactory();
                    break;
                case DUMMY_DAO:
                    daof = new InMemoryDAOFactory();
            }
        } else {
            throw new InvalidDAOFactoryTypeException();
        }
    }

    public abstract User_DAO getUserDAO();

    public abstract AccessLevel_DAO getAccessLevelDAO();

    public abstract WorkTime_DAO getWorkTimeDAO();

    public abstract SollZeiten_DAO getSollZeitenDAO();

    public abstract AbsenceType_DAO getAbsenceTypeDAO();

    public abstract Absence_DAO getAbsenceDAO();

    public abstract Holiday_DAO getHolidayDAO();

    public abstract UserHistory_DAO getUserHistoryDAO();
}
