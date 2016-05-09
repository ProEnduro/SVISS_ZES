/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.interf;

import at.htlpinkafeld.dao.dummy.DummyDAOFactory;
import at.htlpinkafeld.dao.jdbc.JDBCDAOFactory;

/**
 *
 * @author Martin Six
 */
public abstract class DAOFactory {

    private static final int JDBCDAO = 0;
    private static final int DUMMY_DAO = 1;
    private static int daoType;

    private static DAOFactory daof;

    public static synchronized DAOFactory getDAOFactory() {
        if (daof == null) {
            daoType = DUMMY_DAO;
            switch (daoType) {
                case JDBCDAO:
                    daof = new JDBCDAOFactory();
                    break;
                case DUMMY_DAO:
                    daof = new DummyDAOFactory();
                    break;
                default:
                    daof = null;
            }
        }
        return daof;
    }

    public abstract User_DAO getUserDAO();

    public abstract Permission_DAO getPermissionDAO();

    public abstract AccessLevel_DAO getAccessLevelDAO();

    public abstract WorkTime_DAO getWorkTimeDAO();

    public abstract SollZeiten_DAO getSollZeitenDAO();

    public abstract AbsenceType_DAO getAbsenceTypeDAO();

    public abstract Absence_DAO getAbsenceDAO();
}