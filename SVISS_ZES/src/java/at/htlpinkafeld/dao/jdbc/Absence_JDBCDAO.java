/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.dao.util.WrappedConnection;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.AbsenceTypeNew;
import at.htlpinkafeld.pojo.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
public class Absence_JDBCDAO extends Base_JDBCDAO<Absence> implements Absence_DAO {

    public static final String ABSENCEID_COL = "AbsenceID";
    public static final String USERNR_COL = User_JDBCDAO.USERNR_COL;
    public static final String ABSENCETYPENAME_COL = "AbsenceType";
    public static final String STARTTIME_COL = "StartTime";
    public static final String ENDTIME_COL = "EndTime";
    private static final String REASON_COL = "Reason";
    public static final String ACKNOWLEDGED_COL = "Acknowledged";

    public static final String TABLE_NAME = "Absence";
    private static final String[] PRIMARY_KEY = {USERNR_COL, ABSENCEID_COL};
    private static final String[] ALL_COLUMNS = {ABSENCEID_COL, USERNR_COL, ABSENCETYPENAME_COL, STARTTIME_COL, ENDTIME_COL, REASON_COL, ACKNOWLEDGED_COL};

    protected Absence_JDBCDAO() {
        super(TABLE_NAME, ALL_COLUMNS, PRIMARY_KEY);
    }

    @Override
    protected Map<String, Object> entityToMap(Absence entity) {
        Map<String, Object> resMap = new HashMap<>();

        resMap.put(ABSENCEID_COL, entity.getAbsenceID());
        resMap.put(USERNR_COL, entity.getUser().getUserNr());
        resMap.put(ABSENCETYPENAME_COL, entity.getAbsenceType().name());
        resMap.put(STARTTIME_COL, entity.getStartTime());
        resMap.put(ENDTIME_COL, entity.getEndTime());
        resMap.put(REASON_COL, entity.getReason());
        resMap.put(ACKNOWLEDGED_COL, entity.isAcknowledged());

        return resMap;
    }

    @Override
    protected Absence getEntityFromResultSet(ResultSet rs) {
        try {
            return new Absence(rs.getInt(ABSENCEID_COL), new User_JDBCDAO().getUser(rs.getInt(USERNR_COL)), AbsenceTypeNew.valueOf(rs.getString(ABSENCETYPENAME_COL)), rs.getTimestamp(STARTTIME_COL).toLocalDateTime(), rs.getTimestamp(ENDTIME_COL).toLocalDateTime(), rs.getString(REASON_COL), rs.getBoolean(ACKNOWLEDGED_COL));
        } catch (SQLException ex) {
            Logger.getLogger(Absence_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected void updateEntityWithAutoKeys(ResultSet rs, Absence entity) {
        try {
            rs.next();
            entity.setAbsenceID(rs.getInt(1));
        } catch (SQLException ex) {
            Logger.getLogger(Absence_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<Absence> getAbsencesByUser(User u) {
        List<Absence> absences = new ArrayList<>();

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERNR_COL + " = " + u.getUserNr() + " " + SQL_ORDER_BY_LINE)) {

            while (rs.next()) {
                absences.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return absences;
    }

    @Override
    public List<Absence> getAbsencesByAbsenceType(AbsenceTypeNew at) {
        List<Absence> absences = new ArrayList<>();

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ABSENCETYPENAME_COL + " = '" + at.name() + "' " + SQL_ORDER_BY_LINE)) {

            while (rs.next()) {
                absences.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return absences;
    }

    @Override
    public List<Absence> getAbsencesByAbsenceType_User(AbsenceTypeNew at, User u) {
        List<Absence> absences = new ArrayList<>();

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ABSENCETYPENAME_COL + " = '" + at.name() + "' AND " + USERNR_COL + " = " + u.getUserNr() + " " + SQL_ORDER_BY_LINE)) {

            while (rs.next()) {
                absences.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return absences;
    }

    @Override
    public List<Absence> getAbsencesByAcknowledgment(boolean acknowledged) {
        List<Absence> absences = new ArrayList<>();

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ACKNOWLEDGED_COL + " = " + acknowledged + " " + SQL_ORDER_BY_LINE)) {

            while (rs.next()) {
                absences.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return absences;
    }

    @Override
    public List<Absence> getAbsencesByUser_Acknowledgment(User u, boolean acknowledged) {
        List<Absence> absences = new ArrayList<>();

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERNR_COL + " = " + u.getUserNr() + " AND " + ACKNOWLEDGED_COL + " = " + acknowledged + " " + SQL_ORDER_BY_LINE)) {

            while (rs.next()) {
                absences.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return absences;
    }

    @Override
    public List<Absence> getAbsencesBetweenDates(java.util.Date startDateU, java.util.Date endDateU) {
        List<Absence> absences = new ArrayList<>();
        Timestamp startDate = new Timestamp(startDateU.getTime());
        Timestamp endDate = new Timestamp(endDateU.getTime());
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                PreparedStatement stmt = con.getConn().prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE " + STARTTIME_COL + " >= ? AND " + STARTTIME_COL + " < ? OR "
                        + ENDTIME_COL + " >= ? AND " + ENDTIME_COL + " < ? OR " + STARTTIME_COL + " < ? AND " + ENDTIME_COL + " >= ? " + SQL_ORDER_BY_LINE)) {

            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);
            stmt.setTimestamp(3, startDate);
            stmt.setTimestamp(4, endDate);
            stmt.setTimestamp(5, startDate);
            stmt.setTimestamp(6, endDate);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    absences.add(getEntityFromResultSet(rs));
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return absences;
    }

    @Override
    public List<Absence> getAbsencesByUser_BetweenDates(User user, java.util.Date startDateU, java.util.Date endDateU) {
        List<Absence> absences = new ArrayList<>();
        Timestamp startDate = new Timestamp(startDateU.getTime());
        Timestamp endDate = new Timestamp(endDateU.getTime());
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                PreparedStatement stmt = con.getConn().prepareStatement("SELECT * FROM " + TABLE_NAME
                        + " WHERE " + USERNR_COL + " = " + user.getUserNr() + " AND (" + STARTTIME_COL + " >= ? AND " + STARTTIME_COL + " < ? OR "
                        + ENDTIME_COL + " >= ? AND " + ENDTIME_COL + " < ? OR " + STARTTIME_COL + " < ? AND " + ENDTIME_COL + " >= ?) " + SQL_ORDER_BY_LINE)) {

            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);
            stmt.setTimestamp(3, startDate);
            stmt.setTimestamp(4, endDate);
            stmt.setTimestamp(5, startDate);
            stmt.setTimestamp(6, endDate);
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    absences.add(getEntityFromResultSet(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return absences;
    }

    @Override
    public List<Absence> getAbsencesByApprover_Acknowledgment_BetweenDates(User approver, boolean acknowledged, java.util.Date startDateU, java.util.Date endDateU) {
        List<Absence> absences = new ArrayList<>();
        Timestamp startDate = new Timestamp(startDateU.getTime());
        Timestamp endDate = new Timestamp(endDateU.getTime());
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                PreparedStatement stmt = con.getConn().prepareStatement("SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME + " JOIN " + User_JDBCDAO.RELATION_TABLE_NAME
                        + " WHERE " + User_JDBCDAO.REL_APPROVER_COL + " = " + approver.getUserNr() + " AND " + ACKNOWLEDGED_COL + " = " + acknowledged
                        + " AND (" + STARTTIME_COL + " >= ? AND " + STARTTIME_COL + " < ? OR " + ENDTIME_COL + " >= ? AND "
                        + ENDTIME_COL + " < ? OR " + STARTTIME_COL + " < ? AND " + ENDTIME_COL + " >= ?) " + SQL_ORDER_BY_LINE)) {

            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);
            stmt.setTimestamp(3, startDate);
            stmt.setTimestamp(4, endDate);
            stmt.setTimestamp(5, startDate);
            stmt.setTimestamp(6, endDate);
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    absences.add(getEntityFromResultSet(rs));
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return absences;
    }

    @Override
    public List<Absence> getAbsencesByAcknowledgment_BetweenDates(boolean acknowledged, java.util.Date startDateU, java.util.Date endDateU
    ) {
        List<Absence> absences = new ArrayList<>();
        Timestamp startDate = new Timestamp(startDateU.getTime());
        Timestamp endDate = new Timestamp(endDateU.getTime());
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                PreparedStatement stmt = con.getConn().prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
                        + ACKNOWLEDGED_COL + " = " + acknowledged + " AND (" + STARTTIME_COL + " >= ? AND " + STARTTIME_COL + " < ? OR " + ENDTIME_COL + " >= ? AND "
                        + ENDTIME_COL + " < ? OR " + STARTTIME_COL + " < ? AND " + ENDTIME_COL + " >= ?) " + SQL_ORDER_BY_LINE)) {

            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);
            stmt.setTimestamp(3, startDate);
            stmt.setTimestamp(4, endDate);
            stmt.setTimestamp(5, startDate);
            stmt.setTimestamp(6, endDate);
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    absences.add(getEntityFromResultSet(rs));
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return absences;
    }

    @Override
    public void deleteAllAbsenceFromUser(User o) {
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement()) {

            stmt.execute("DELETE FROM " + TABLE_NAME + " WHERE + " + USERNR_COL + " = " + o.getUserNr() + ";");
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
