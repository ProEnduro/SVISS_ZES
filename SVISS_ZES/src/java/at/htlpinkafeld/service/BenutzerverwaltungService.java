/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.UserHistory_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserHistoryEntry;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author msi
 */
public class BenutzerverwaltungService {

    private static final User_DAO userdao;
    private static final UserHistory_DAO UHDAO;

    static {
        userdao = DAOFactory.getDAOFactory().getUserDAO();
        UHDAO = DAOFactory.getDAOFactory().getUserHistoryDAO();
    }

    public static User getUser(String selectedUser) {
        return userdao.getUserByUsername(selectedUser);
    }

    public BenutzerverwaltungService() {
    }

    public static List<User> getUserList() {
        return userdao.getList();
    }

    public static void insertUser(User u) {
        userdao.insert(u);
        UHDAO.insert(new UserHistoryEntry(LocalDate.now().withDayOfMonth(1).atStartOfDay().minusNanos(1), u, u.getOverTimeLeft(), u.getVacationLeft()));
    }

    public static void updateUser(User u) {
        userdao.update(u);
    }

    public static User getUser(int id) {
        return userdao.getUser(id);
    }

    public static User getUserByUsername(String username) {
        return userdao.getUserByUsername(username);
    }

    public static List<User> getUserByDisabled(Boolean disabled) {
        return userdao.getUserByDisabled(disabled);
    }

    public static List<User> getUserByAccessLevel(AccessLevel accessLevel) {
        return userdao.getUserByAccessLevel(accessLevel);
    }

    public static void updateApproverOfUser(User user) {
        userdao.updateApproverOfUser(user);
    }

    public static void removeApprover(User approver) {
        userdao.removeApprover(approver);
    }

    public static User getUserByEmail(String email) {
        return userdao.getUserByEmail(email);
    }
}
