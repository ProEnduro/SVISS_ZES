/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.UserHistory_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserHistoryEntry;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
class UserHistory_InMemoryDAO extends Base_InMemoryDAO<UserHistoryEntry> implements UserHistory_DAO {

    public UserHistory_InMemoryDAO() {
        super(new ArrayList<>());
        User_DAO uDAO = new User_InMemoryDAO();

        for (User u : uDAO.getList()) {
            super.insert(new UserHistoryEntry(LocalDateTime.now(), u, u.getOverTimeLeft(), u.getVacationLeft()));
        }
    }

    @Override
    protected UserHistoryEntry clone(UserHistoryEntry entity) {
        return new UserHistoryEntry(entity);
    }

    @Override
    protected void setID(UserHistoryEntry entity, int id) {
    }

    @Override
    public List<UserHistoryEntry> getUserHistoryEntrysByUser(User u) {
        List<UserHistoryEntry> historyEntrys = new LinkedList<>();
        for (UserHistoryEntry uhe : super.getList()) {
            if (uhe.getUser().equals(u)) {
                historyEntrys.add(clone(uhe));
            }
        }
        return historyEntrys;
    }

    @Override
    public List<UserHistoryEntry> getUserHistoryEntrysByUser_BetweenDates(User user, LocalDate startDate, LocalDate endDate) {
        List<UserHistoryEntry> historyEntrys = new LinkedList<>();
        for (UserHistoryEntry uhe : super.getList()) {
            if (uhe.getUser().equals(user)) {
                if ((uhe.getTimestamp().toLocalDate().isAfter(startDate) || uhe.getTimestamp().toLocalDate().isEqual(startDate))
                        && uhe.getTimestamp().toLocalDate().isBefore(endDate)) {
                    historyEntrys.add(clone(uhe));
                }
            }
        }
        return historyEntrys;
    }

}
