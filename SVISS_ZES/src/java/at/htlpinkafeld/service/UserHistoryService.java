/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.UserHistory_DAO;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserHistoryEntry;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author msi
 */
public class UserHistoryService {

    private static UserHistory_DAO userHistoryDAO;

    static {
        userHistoryDAO = DAOFactory.getDAOFactory().getUserHistoryDAO();
    }

    public static List<UserHistoryEntry> getUserHistoryEntries() {
        return userHistoryDAO.getList();
    }

    public static List<UserHistoryEntry> getUserHistoryEntriesForUser(User u) {
        return userHistoryDAO.getUserHistoryEntrysByUser(u);
    }

    public static List<UserHistoryEntry> getUserHistoryEntriesForUserBetweenDates(User u, LocalDate start, LocalDate end) {
        return userHistoryDAO.getUserHistoryEntrysByUser_BetweenDates(u, start, end);
    }

    public static void updateUserHistoryEntry(UserHistoryEntry uhe) {
        userHistoryDAO.update(uhe);
    }

    public static void insertUserHistoryEntry(UserHistoryEntry uhe) {
        userHistoryDAO.insert(uhe);
    }
    
    public static List<UserHistoryEntry> getUserHistoryEntriesBetweenDates(LocalDate start, LocalDate end) {
        return userHistoryDAO.getUserHistoryEntriesBetweenDates(start, end);
    }
}
