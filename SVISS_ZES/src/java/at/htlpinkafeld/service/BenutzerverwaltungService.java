/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.UserHistory_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.dao.util.DAODML_Observer;
import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserHistoryEntry;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author msi
 */
public class BenutzerverwaltungService {

    private static final User_DAO USER_DAO;
    private static final UserHistory_DAO UHDAO;

    static {
        USER_DAO = DAOFactory.getDAOFactory().getUserDAO();
        UHDAO = DAOFactory.getDAOFactory().getUserHistoryDAO();
    }

    public static User getUser(String selectedUser) {
        return USER_DAO.getUserByUsername(selectedUser);
    }

    public static void deleteUser(User u) {
        USER_DAO.delete(u);
    }

    private BenutzerverwaltungService() {
    }

    public static List<User> getUserList() {
        return USER_DAO.getList();
    }

    public static void insertUser(User u) {
        USER_DAO.insert(u);
        LocalDateTime ldt = u.getHiredate().atStartOfDay();
        UHDAO.insert(new UserHistoryEntry(ldt, u, u.getOverTimeLeft(), u.getVacationLeft()));
        ldt = ldt.withDayOfMonth(1).plusMonths(2).with(LocalTime.MIN).minusSeconds(1);
        for (LocalDateTime now = LocalDateTime.now(); ldt.isBefore(now); ldt = ldt.plusMonths(1)) {
            UHDAO.insert(new UserHistoryEntry(ldt, u, u.getOverTimeLeft(), u.getVacationLeft()));
        }
    }

    public static void updateUser(User u) {
        USER_DAO.update(u);
    }

    public static User getUser(int id) {
        return USER_DAO.getUser(id);
    }

    public static User getUserByUsername(String username) {
        return USER_DAO.getUserByUsername(username);
    }

    public static List<User> getUserByDisabled(Boolean disabled) {
        return USER_DAO.getUserByDisabled(disabled);
    }

    public static List<User> getUserByAccessLevel(AccessLevel accessLevel) {
        return USER_DAO.getUserByAccessLevel(accessLevel);
    }

    public static void updateApproverOfUser(User user) {
        USER_DAO.updateApproverOfUser(user);
    }

    public static void removeApprover(User approver) {
        USER_DAO.removeApprover(approver);
    }

    public static User getUserByEmail(String email) {
        return USER_DAO.getUserByEmail(email);
    }

    public static void addUserObserver(DAODML_Observer o) {
        USER_DAO.addObserver(o);
    }

    public static void deleteUserObserver(DAODML_Observer o) {
        USER_DAO.deleteObserver(o);
    }

}
