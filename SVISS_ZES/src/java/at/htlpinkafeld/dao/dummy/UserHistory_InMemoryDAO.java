/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.UserHistory_DAO;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserHistoryEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import sun.nio.cs.HistoricallyNamedCharset;

/**
 *
 * @author Martin Six
 */
class UserHistory_InMemoryDAO extends Base_InMemoryDAO<UserHistoryEntry> implements UserHistory_DAO {

    public UserHistory_InMemoryDAO() {
        super(new ArrayList<>());
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

}
