/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.dao.util.DAOException;
import at.htlpinkafeld.dao.util.WrappedConnection;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.service.TimeConverterService;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
public class SollZeiten_JDBCDAO extends Base_JDBCDAO<SollZeit> implements SollZeiten_DAO {

    public static final String DAYID_COL = "DayID";
    public static final String USERNR_COL = User_JDBCDAO.USERNR_COL;
    public static final String SOLLSTARTTIME_COL = "SollStartTime";
    public static final String SOLLENDTIME_COL = "SollEndTime";

    public static final String INSERTED_COL = "Inserted";

    public static final String TABLE_NAME = "SollZeiten";
    public static final String[] PRIMARY_KEY = {USERNR_COL, DAYID_COL};
    public static final String[] ALL_COLUMNS = {DAYID_COL, USERNR_COL, SOLLSTARTTIME_COL, SOLLENDTIME_COL};

    protected SollZeiten_JDBCDAO() {
        super(TABLE_NAME, ALL_COLUMNS, PRIMARY_KEY);
    }

    @Override
    protected Map<String, Object> entityToMap(SollZeit entity) {
        Map<String, Object> resMap = new HashMap<>();

        resMap.put(DAYID_COL, SollZeit.getDBShortFromDayOfWeek(entity.getDay()));
        resMap.put(USERNR_COL, entity.getUser().getUserNr());
        resMap.put(SOLLSTARTTIME_COL, entity.getSollStartTime());
        resMap.put(SOLLENDTIME_COL, entity.getSollEndTime());

        return resMap;
    }

    @Override
    protected SollZeit getEntityFromResultSet(ResultSet rs) {
        try {
            return new SollZeit(SollZeit.getDayOfWeekFromDBShort(rs.getString(DAYID_COL)), new User_JDBCDAO().getUser(rs.getInt(USERNR_COL)), rs.getTime(SOLLSTARTTIME_COL).toLocalTime(), rs.getTime(SOLLENDTIME_COL).toLocalTime());
        } catch (SQLException ex) {
            Logger.getLogger(SollZeiten_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected void updateEntityWithAutoKeys(ResultSet rs, SollZeit entity) {
//        try {
//        rs.next();
//            entity.setDay(DayOfWeek.valueOf(rs.getString(1)));
//        } catch (SQLException ex) {
//            Logger.getLogger(SollZeiten_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @Override
    public List<SollZeit> getList() throws DAOException {
        List<SollZeit> sollZeiten = new LinkedList<>();

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " sz1 WHERE sz1." + INSERTED_COL
                        + " =  ( select MAX( sz2." + INSERTED_COL + ")  from " + TABLE_NAME + " sz2 where  sz1." + DAYID_COL + " = sz2." + DAYID_COL + " ) " + " GROUP BY " + DAYID_COL + ", " + USERNR_COL + SQL_ORDER_BY_LINE)) {
            while (rs.next()) {
                sollZeiten.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sollZeiten;
    }

    /**
     * Update should never be called
     *
     * @param o
     */
    @Override
    @Deprecated
    public void update(SollZeit o) {
        throw new UnsupportedOperationException();
    }

    /**
     * Delete should never be called. Use insert with a time of 0 instead
     *
     * @param o
     */
    @Override
    public void delete(SollZeit o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SollZeit> getSollZeitenByUser(User u) {
        List<SollZeit> sollZeiten = new LinkedList<>();
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " sz1 WHERE sz1." + USERNR_COL + " = " + u.getUserNr() + " AND sz1." + INSERTED_COL
                        + " =  ( select MAX( sz2." + INSERTED_COL + ")  from " + TABLE_NAME + " sz2 where  sz1." + DAYID_COL + " = sz2." + DAYID_COL
                        + " AND sz1." + USERNR_COL + " = sz2." + USERNR_COL + " ) " + SQL_ORDER_BY_LINE)) {
            while (rs.next()) {
                sollZeiten.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sollZeiten;
    }

    @Override
    public SollZeit getSollZeitenByUser_DayOfWeek(User u, DayOfWeek d) {
        SollZeit sollZeit = null;

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " sz1 WHERE sz1." + USERNR_COL + " = " + u.getUserNr()
                        + " AND sz1." + DAYID_COL + " = '" + SollZeit.getDBShortFromDayOfWeek(d) + "' AND sz1." + INSERTED_COL + " AND sz1." + USERNR_COL + " = sz2." + USERNR_COL
                        + " =  ( select MAX( " + INSERTED_COL + ")  from " + TABLE_NAME + " sz2 where  s1." + DAYID_COL + " = sz2." + DAYID_COL
                        + " AND sz1." + USERNR_COL + " = sz2." + USERNR_COL + " ) " + SQL_ORDER_BY_LINE)) {

            if (rs.next()) {
                sollZeit = getEntityFromResultSet(rs);
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sollZeit;
    }

    @Override
    public SollZeit getSollZeitenByUser_DayOfWeek_ValidDate(User u, DayOfWeek d, LocalDateTime ldt) {
        SollZeit sollZeit = null;

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                PreparedStatement stmt = con.getConn().prepareStatement("SELECT * FROM " + TABLE_NAME + " sz1 WHERE sz1." + USERNR_COL + " = " + u.getUserNr()
                        + " AND sz1." + DAYID_COL + " = '" + SollZeit.getDBShortFromDayOfWeek(d) + "' AND sz1." + INSERTED_COL
                        + " =  ( select MAX( " + INSERTED_COL + ")  from " + TABLE_NAME + " sz2 where  sz1." + DAYID_COL + " = sz2." + DAYID_COL 
                        + " AND sz1." + USERNR_COL + " = sz2." + USERNR_COL + " AND sz2." + INSERTED_COL + " <= ? " + " ) " + SQL_ORDER_BY_LINE)) {
            System.out.println("SELECT * FROM " + TABLE_NAME + " sz1 WHERE sz1." + USERNR_COL + " = " + u.getUserNr()
                        + " AND sz1." + DAYID_COL + " = '" + SollZeit.getDBShortFromDayOfWeek(d) + "' AND sz1." + INSERTED_COL
                        + " =  ( select MAX( " + INSERTED_COL + ")  from " + TABLE_NAME + " sz2 where  sz1." + DAYID_COL + " = sz2." + DAYID_COL 
                        + " AND sz1." + USERNR_COL + " = sz2." + USERNR_COL + " AND sz2." + INSERTED_COL + " <= ? " + " ) " + SQL_ORDER_BY_LINE);
            stmt.setTimestamp(1, Timestamp.valueOf(ldt));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                sollZeit = getEntityFromResultSet(rs);
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sollZeit;
    }

}
