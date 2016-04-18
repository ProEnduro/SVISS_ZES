/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao;

import at.htlpinkafeld.dao.interf.AbsenceType_DAO;
import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.dao.interf.AccessLevel_DAO;
import at.htlpinkafeld.dao.interf.WorkTime_DAO;
import at.htlpinkafeld.dao.interf.Permission_DAO;
import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.dao.dummy.AbsenceType_DummyDAO;
import at.htlpinkafeld.dao.dummy.Absence_DummyDAO;
import at.htlpinkafeld.dao.dummy.AccessLevel_DummyDAO;
import at.htlpinkafeld.dao.dummy.Permission_DummyDAO;
import at.htlpinkafeld.dao.dummy.SollZeiten_DummyDAO;
import at.htlpinkafeld.dao.dummy.User_DummyDAO;
import at.htlpinkafeld.dao.dummy.WorkTime_DummyDAO;

/**
 *
 * @author Martin Six
 */
public class DAOFactory {

    public static User_DAO getUserDAO() {
        return new User_DummyDAO();
    }

    public static Permission_DAO getPermissionDAO() {
        return new Permission_DummyDAO();
    }

    public static AccessLevel_DAO getAccessLevelDAO() {
        return new AccessLevel_DummyDAO();
    }

    public static WorkTime_DAO getWorkTimeDAO() {
        return new WorkTime_DummyDAO();
    }

    public static SollZeiten_DAO getSollZeitenDAO() {
        return new SollZeiten_DummyDAO();
    }

    public static AbsenceType_DAO getAbsenceTypeDAO() {
        return new AbsenceType_DummyDAO();
    }

    public static Absence_DAO getAbsenceDAO() {
        return new Absence_DummyDAO();
    }
}
