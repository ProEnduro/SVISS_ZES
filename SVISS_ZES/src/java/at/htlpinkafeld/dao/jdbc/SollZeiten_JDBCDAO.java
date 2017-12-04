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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public static final String SOLLZEITENID_COL = "SollZeitenID";
    public static final String USERNR_COL = User_JDBCDAO.USERNR_COL;
    public static final String INSERTED_COL = "Inserted";

    public static final String TABLE_NAME = "SollZeiten";

    private static final String[] PRIMARY_KEY = {SOLLZEITENID_COL};
    private static final String[] ALL_COLUMNS = {SOLLZEITENID_COL, USERNR_COL, INSERTED_COL};

    private SollZeitenPerDay_JDBCDAO zeitenPerDay_JDBCDAO;

    protected SollZeiten_JDBCDAO() {
        super(TABLE_NAME, ALL_COLUMNS, PRIMARY_KEY);
        zeitenPerDay_JDBCDAO = new SollZeitenPerDay_JDBCDAO();
    }

    @Override
    protected Map<String, Object> entityToMap(SollZeit entity) {
        Map<String, Object> resMap = new HashMap<>();

        //add attributes which are inserted into the main table
        resMap.put(SOLLZEITENID_COL, entity.getSollZeitID());
        resMap.put(USERNR_COL, entity.getUser().getUserNr());
        resMap.put(INSERTED_COL, entity.getValidFrom());

        return resMap;
    }

    @Override
    protected SollZeit getEntityFromResultSet(ResultSet rs) {
        try {

            SollZeit sz = new SollZeit(rs.getInt(SOLLZEITENID_COL), new User_JDBCDAO().getUser(rs.getInt(USERNR_COL)), null, null);
            zeitenPerDay_JDBCDAO.selectAndSetSollZeit(sz);
            return sz;
        } catch (SQLException ex) {
            Logger.getLogger(SollZeiten_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected void updateEntityWithAutoKeys(ResultSet rs, SollZeit entity) {
        try {
            rs.next();
            entity.setSollZeitID(rs.getInt(1));
        } catch (SQLException ex) {
            Logger.getLogger(SollZeiten_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    @Override
//    public List<SollZeit> getList() throws DAOException {
//        List<SollZeit> sollZeiten = new LinkedList<>();
//        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
//                Statement stmt = con.getConn().createStatement();
//                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " sz1 WHERE sz1." + INSERTED_COL
//                        + " =  ( select MAX( sz2." + INSERTED_COL + ")  from " + TABLE_NAME + " sz2 where  sz1." + DAYID_COL + " = sz2." + DAYID_COL + " ) " + " GROUP BY " + DAYID_COL + " " + SQL_ORDER_BY_LINE)) {
//
//            while (rs.next()) {
//                if (!(rs.getTime(SOLLSTARTTIME_COL).toLocalTime().equals(LocalTime.MIN) && rs.getTime(SOLLENDTIME_COL).toLocalTime().equals(LocalTime.MIN))) {
//                    sollZeiten.add(getEntityFromResultSet(rs));
//                }
//            }
//
//        } catch (SQLException ex) {
//            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return sollZeiten;
//}
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
    @Deprecated
    public void delete(SollZeit o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(SollZeit sz) throws DAOException {
        try {
            super.insert(sz);
            if (sz.getSollStartTimeMap() != null) {
                sz.getSollStartTimeMap().keySet().forEach((dow) -> {
                    zeitenPerDay_JDBCDAO.insertSollZeit(sz.getSollZeitID(), dow, sz.getSollStartTime(dow), sz.getSollEndTime(dow));
                });
            }
        } catch (DAOException ex) {
            Logger.getLogger(SollZeiten_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<SollZeit> getSollZeitenByUser(User u) {
        List<SollZeit> sollZeiten = new LinkedList<>();
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERNR_COL + " = " + u.getUserNr() + SQL_ORDER_BY_LINE)) {
            while (rs.next()) {
                sollZeiten.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return sollZeiten;
    }

    @Override
    public SollZeit getSollZeitenByUser_Current(User u) {
        SollZeit sollZeit = null;

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " sz1 WHERE sz1." + USERNR_COL + " = " + u.getUserNr()
                        + " AND sz1." + INSERTED_COL
                        + " =  ( select MAX( " + INSERTED_COL + ")  from " + TABLE_NAME + " sz2 WHERE sz1." + USERNR_COL + " = sz2." + USERNR_COL + " ) " + SQL_ORDER_BY_LINE)) {
            if (rs.next()) {
                sollZeit = getEntityFromResultSet(rs);

            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sollZeit;
    }

    @Override
    public SollZeit getSollZeitenByUser_ValidDate(User u, LocalDateTime ldt) {
        SollZeit sollZeit = null;

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                PreparedStatement stmt = con.getConn().prepareStatement("SELECT * FROM " + TABLE_NAME + " sz1 WHERE sz1." + USERNR_COL + " = " + u.getUserNr()
                        + " AND sz1." + INSERTED_COL
                        + " =  ( select MAX( " + INSERTED_COL + ")  from " + TABLE_NAME + " sz2 WHERE sz1." + USERNR_COL + " = sz2." + USERNR_COL
                        + " AND sz2." + INSERTED_COL + " <= ? " + " ) " + SQL_ORDER_BY_LINE)) {
            stmt.setTimestamp(1, Timestamp.valueOf(ldt));

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    sollZeit = getEntityFromResultSet(rs);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sollZeit;
    }

    @Override
    public void deleteAllSollZeitenFromUser(User o) {
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement()) {

            stmt.execute("DELETE FROM " + TABLE_NAME + " WHERE + " + USERNR_COL + " = " + o.getUserNr() + ";");
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static class SollZeitenPerDay_JDBCDAO {

        private static final String SUB_TABLE_NAME = "SollZeitPerDay";
        private static final String DAYID_COL = "DayID";
        private static final String SOLLSTARTTIME_COL = "SollStartTime";
        private static final String SOLLENDTIME_COL = "SollEndTime";

        public void insertSollZeit(int sollZeitId, DayOfWeek dow, LocalTime startTime, LocalTime endTime) {
            try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                    PreparedStatement stmt = con.getConn().prepareStatement("INSERT into " + SUB_TABLE_NAME
                            + " (" + SOLLZEITENID_COL + ", " + DAYID_COL + ", " + SOLLSTARTTIME_COL + ", " + SOLLENDTIME_COL
                            + ") VALUES(?, ?, ?, ?);")) {
                stmt.setInt(1, sollZeitId);
                stmt.setString(2, SollZeit.getDBShortFromDayOfWeek(dow));
                stmt.setTime(3, Time.valueOf(startTime));
                stmt.setTime(4, Time.valueOf(endTime));

                stmt.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(SollZeitenPerDay_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void deleteSollZeit(int sollZeitId) {
            try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                    PreparedStatement stmt = con.getConn().prepareStatement("DELETE from " + SUB_TABLE_NAME + " WHERE "
                            + SOLLZEITENID_COL + " = ?")) {
                stmt.setInt(1, sollZeitId);

                stmt.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(SollZeitenPerDay_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void selectAndSetSollZeit(SollZeit sz) {
            try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                    PreparedStatement stmt = con.getConn().prepareStatement("SELECT * from " + SUB_TABLE_NAME + " WHERE "
                            + SOLLZEITENID_COL + " = ?")) {
                stmt.setInt(1, sz.getSollZeitID());
                try (ResultSet rs = stmt.executeQuery()) {

                    Map<DayOfWeek, LocalTime> startTimeMap = new HashMap<>();
                    Map<DayOfWeek, LocalTime> endTimeMap = new HashMap<>();

                    while (rs.next()) {
                        DayOfWeek dow = SollZeit.getDayOfWeekFromDBShort(rs.getString(DAYID_COL));
                        startTimeMap.put(dow, rs.getTime(SOLLSTARTTIME_COL).toLocalTime());
                        endTimeMap.put(dow, rs.getTime(SOLLENDTIME_COL).toLocalTime());
                    }

                    sz.setSollStartTimeMap(startTimeMap);
                    sz.setSollEndTimeMap(endTimeMap);
                }

            } catch (SQLException ex) {
                Logger.getLogger(SollZeitenPerDay_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
