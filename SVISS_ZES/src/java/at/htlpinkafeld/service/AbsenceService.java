/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.AbsenceType_DAO;
import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.dao.util.DAOException;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.User;
import java.util.Date;
import java.util.List;

/**
 * A serivce which wraps {@link Absence_DAO} and {@link AbsenceType_DAO} with
 * static functions, which are then delegated to the DAOs.
 *
 * @author msi
 */
public class AbsenceService {

    private static final Absence_DAO ABSENCE_DAO;

    static {
        ABSENCE_DAO = DAOFactory.getDAOFactory().getAbsenceDAO();
    }

    public static List<Absence> getAbsenceByUserAndUnacknowledged(User u) {
        return ABSENCE_DAO.getAbsencesByUser_Acknowledgment(u, false);
    }

    public static List<Absence> getAbsenceByUser(User u) {
        return ABSENCE_DAO.getAbsencesByUser(u);
    }

    public static void insertAbsence(Absence o) throws DAOException {
        ABSENCE_DAO.insert(o);
    }

    public static void updateAbsence(Absence a) {
        ABSENCE_DAO.update(a);
    }

    public static List<Absence> getAllUnacknowledged() {
        return ABSENCE_DAO.getAbsencesByAcknowledgment(false);
    }

    public static List<Absence> getAllAcknowledged() {
        return ABSENCE_DAO.getAbsencesByAcknowledgment(true);
    }

    public static List<Absence> getAbsenceByUserAndAcknowledged(User u) {
        return ABSENCE_DAO.getAbsencesByUser_Acknowledgment(u, true);
    }

    public static void removeAbsence(Absence a) {
        ABSENCE_DAO.delete(a);
    }

    public static List<Absence> getAllAbsences() {
        return ABSENCE_DAO.getList();
    }

    public static void deleteAbsence(Absence absence) {
        ABSENCE_DAO.delete(absence);
    }

    public static List<Absence> getAbsencesBetweenDates(Date startDateU, Date endDateU) {
        return ABSENCE_DAO.getAbsencesBetweenDates(startDateU, endDateU);
    }

    public static List<Absence> getAbsencesByUserBetweenDates(User user, Date startDateU, Date endDateU) {
        return ABSENCE_DAO.getAbsencesByUser_BetweenDates(user, startDateU, endDateU);
    }

}
