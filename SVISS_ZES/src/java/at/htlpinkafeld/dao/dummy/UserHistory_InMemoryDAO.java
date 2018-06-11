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

        uDAO.getList().forEach((u) -> {
            super.insert(new UserHistoryEntry(LocalDateTime.now(), u, u.getOverTimeLeft(), u.getVacationLeft()));
        });
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
        super.getList().stream().filter((uhe) -> (uhe.getUser().equals(u))).forEachOrdered((uhe) -> {
            historyEntrys.add(clone(uhe));
        });
        return historyEntrys;
    }

    @Override
    public List<UserHistoryEntry> getUserHistoryEntrysByUser_BetweenDates(User user, LocalDate startDate, LocalDate endDate) {
        List<UserHistoryEntry> historyEntrys = new LinkedList<>();
        super.getList().stream().filter((uhe) -> (uhe.getUser().equals(user))).filter((uhe) -> ((uhe.getTimestamp().toLocalDate().isAfter(startDate) || uhe.getTimestamp().toLocalDate().isEqual(startDate))
                && uhe.getTimestamp().toLocalDate().isBefore(endDate))).forEachOrdered((uhe) -> {
                    historyEntrys.add(clone(uhe));
        });
        return historyEntrys;
    }

    @Override
    public void deleteAllUserHistoryFromUser(User o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<UserHistoryEntry> getUserHistoryEntriesBetweenDates(LocalDate start, LocalDate end) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
