/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao;

import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.pojo.SollZeiten;
import at.htlpinkafeld.pojo.User;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Six
 */
class SollZeiten_JDBCDAO extends Base_JDBCDAO<SollZeiten> implements SollZeiten_DAO {

    public static final String DAYID_COL = "DayID";
    public static final String USERNR_COL = User_JDBCDAO.USERNR_COL;
    public static final String SOLLSTARTTIME_COL = "SollStartTime";
    public static final String SOLLENDTIME_COL = "SollEndTime";

    public static final String TABLE_NAME = "SollZeiten";
    public static final String[] PRIMARY_KEY = {DAYID_COL, USERNR_COL};
    public static final String[] ALL_COLUMNS = {DAYID_COL, USERNR_COL, SOLLSTARTTIME_COL, SOLLENDTIME_COL};

    public SollZeiten_JDBCDAO() {
        super(TABLE_NAME, ALL_COLUMNS, PRIMARY_KEY);
    }

    @Override
    protected Map<String, Object> entityToMap(SollZeiten entity) {
        Map<String, Object> resMap = new HashMap<>();

        resMap.put(DAYID_COL, SollZeiten.getDBShortFromDayOfWeek(entity.getDay()));
        resMap.put(USERNR_COL, entity.getUser().getUserNr());
        resMap.put(SOLLSTARTTIME_COL, entity.getSollStartTime());
        resMap.put(SOLLENDTIME_COL, entity.getSollEndTime());

        return resMap;
    }

    @Override
    protected SollZeiten getEntityFromResultSet(ResultSet rs) {
        try {
            return new SollZeiten(SollZeiten.getDayOfWeekFromDBShort(rs.getString(DAYID_COL)), new User_JDBCDAO().getUser(rs.getInt(USERNR_COL)), rs.getTime(SOLLSTARTTIME_COL).toLocalTime(), rs.getTime(SOLLENDTIME_COL).toLocalTime());
        } catch (SQLException ex) {
            Logger.getLogger(SollZeiten_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected void updateEntityWithAutoKeys(ResultSet rs, SollZeiten entity) {
        try {
            entity.setDay(DayOfWeek.valueOf(rs.getString(DAYID_COL)));
        } catch (SQLException ex) {
            Logger.getLogger(SollZeiten_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<SollZeiten> getSollZeitenByUser(User u) {
        List<SollZeiten> sollZeiten = new LinkedList<>();

        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERNR_COL + " = " + u.getUserNr())) {

            while (rs.next()) {
                sollZeiten.add(new SollZeiten(DayOfWeek.valueOf(rs.getString(DAYID_COL)), u, rs.getTime(SOLLSTARTTIME_COL).toLocalTime(), rs.getTime(SOLLENDTIME_COL).toLocalTime()));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sollZeiten;
    }

}
