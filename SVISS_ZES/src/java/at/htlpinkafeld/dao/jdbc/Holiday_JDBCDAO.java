/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.Holiday_DAO;
import static at.htlpinkafeld.dao.jdbc.SollZeiten_JDBCDAO.ALL_COLUMNS;
import static at.htlpinkafeld.dao.jdbc.SollZeiten_JDBCDAO.DAYID_COL;
import static at.htlpinkafeld.dao.jdbc.SollZeiten_JDBCDAO.PRIMARY_KEY;
import static at.htlpinkafeld.dao.jdbc.SollZeiten_JDBCDAO.SOLLENDTIME_COL;
import static at.htlpinkafeld.dao.jdbc.SollZeiten_JDBCDAO.SOLLSTARTTIME_COL;
import static at.htlpinkafeld.dao.jdbc.SollZeiten_JDBCDAO.TABLE_NAME;
import static at.htlpinkafeld.dao.jdbc.SollZeiten_JDBCDAO.USERNR_COL;
import at.htlpinkafeld.dao.util.WrappedConnection;
import at.htlpinkafeld.pojo.Holiday;
import at.htlpinkafeld.pojo.SollZeiten;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
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
    public List<Holiday> getHolidayBetweenDates(Date startDateU, Date endDateU) {
        List<Holiday> holidays = new ArrayList<>();

        Date startDate = new Date(startDateU.getTime());
        Date endDate = new Date(endDateU.getTime());

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + HOLIDAYDATE_COL + " >= '" + startDate + "' AND  " + HOLIDAYDATE_COL + " < '" + endDate + "' " + SQL_ORDER_BY_LINE)) {

            while (rs.next()) {
                holidays.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return holidays;
    }

}
