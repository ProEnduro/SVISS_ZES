/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.Holiday_DAO;
import at.htlpinkafeld.dao.util.WrappedConnection;
import at.htlpinkafeld.pojo.Holiday;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Six
 */
public class Holiday_JDBCDAO extends Base_JDBCDAO<Holiday> implements Holiday_DAO {

    private static final String HOLIDAYDATE_COL = "HolidayDate";
    private static final String HOLIDAYCOMMENT_COL = "HolidayComment";

    private static final String TABLE_NAME = "Holiday";
    private static final String[] PRIMARY_KEY = {HOLIDAYDATE_COL};
    private static final String[] ALL_COLUMNS = {HOLIDAYDATE_COL, HOLIDAYCOMMENT_COL};

    protected Holiday_JDBCDAO() {
        super(TABLE_NAME, ALL_COLUMNS, PRIMARY_KEY);
    }

    @Override
    protected Map<String, Object> entityToMap(Holiday entity) {
        Map<String, Object> resMap = new HashMap<>();

        resMap.put(HOLIDAYDATE_COL, entity.getHolidayDate());
        resMap.put(HOLIDAYCOMMENT_COL, entity.getHolidayComment());

        return resMap;
    }

    @Override
    protected Holiday getEntityFromResultSet(ResultSet rs) {
        try {
            return new Holiday(rs.getDate(HOLIDAYDATE_COL).toLocalDate(), rs.getString(HOLIDAYCOMMENT_COL));
        } catch (SQLException ex) {
            Logger.getLogger(Holiday_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected void updateEntityWithAutoKeys(ResultSet rs, Holiday entity) {
    }

    @Override
    public List<Holiday> getHolidayBetweenDates(java.util.Date startDateU, java.util.Date endDateU) {
        List<Holiday> holidays = new ArrayList<>();

        Timestamp startDate = new Timestamp(startDateU.getTime());
        Timestamp endDate = new Timestamp(endDateU.getTime());

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                PreparedStatement stmt = con.getConn().prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE (" + HOLIDAYDATE_COL + " >= ? AND  " + HOLIDAYDATE_COL + " < ?) OR ("
                        + HOLIDAYDATE_COL + " = ? AND " + HOLIDAYDATE_COL + " = ?) " + SQL_ORDER_BY_LINE)) {
            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);
            stmt.setTimestamp(3, startDate);
            stmt.setTimestamp(4, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    holidays.add(getEntityFromResultSet(rs));
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return holidays;
    }

}
