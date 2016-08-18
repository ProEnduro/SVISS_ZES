/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.UserHistory_DAO;
import at.htlpinkafeld.dao.util.WrappedConnection;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserHistoryEntry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Six
 */
class UserHistory_JDBCDAO extends Base_JDBCDAO<UserHistoryEntry> implements UserHistory_DAO {

    public static final String MONTHTS_COL = "MonthTS";
    public static final String USERNR_COL = User_JDBCDAO.USERNR_COL;
    public static final String OVERTIME_COL = "Overtime";
    public static final String VACATION_COL = "Vacation";

    public static final String TABLE_NAME = "UserHolidayOvertimeHistory";
    public static final String[] PRIMARY_KEY = {MONTHTS_COL, USERNR_COL};
    public static final String[] ALL_COLUMNS = {MONTHTS_COL, USERNR_COL, OVERTIME_COL, VACATION_COL};

    public UserHistory_JDBCDAO() {
        super(TABLE_NAME, ALL_COLUMNS, PRIMARY_KEY);
    }

    @Override
    protected Map<String, Object> entityToMap(UserHistoryEntry entity) {
        Map<String, Object> resMap = new HashMap<>();

        resMap.put(MONTHTS_COL, entity.getTimestamp());
        resMap.put(USERNR_COL, entity.getUser().getUserNr());
        resMap.put(OVERTIME_COL, entity.getOvertime());
        resMap.put(VACATION_COL, entity.getVacation());

        return resMap;
    }

    @Override
    protected UserHistoryEntry getEntityFromResultSet(ResultSet rs) {
        try {
            return new UserHistoryEntry(rs.getTimestamp(TABLE_NAME).toLocalDateTime(), new User_JDBCDAO().getUser(rs.getInt(USERNR_COL)), rs.getInt(OVERTIME_COL), rs.getInt(VACATION_COL));
        } catch (SQLException ex) {
            Logger.getLogger(SollZeiten_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected void updateEntityWithAutoKeys(ResultSet rs, UserHistoryEntry entity) {

    }

    @Override
    public List<UserHistoryEntry> getUserHistoryEntrysByUser(User u) {
        List<UserHistoryEntry> historyEntrys = new ArrayList<>();

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERNR_COL + " = " + u.getUserNr() + " " + SQL_ORDER_BY_LINE)) {

            while (rs.next()) {
                historyEntrys.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return historyEntrys;
    }
}