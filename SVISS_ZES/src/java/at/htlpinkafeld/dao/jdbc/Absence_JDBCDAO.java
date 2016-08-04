/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.dao.util.WrappedConnection;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.AbsenceType;
import at.htlpinkafeld.pojo.User;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    public static final String ABSENCETYPEID_COL = "AbsenceTypeID";
    public static final String STARTTIME_COL = "StartTime";
    public static final String ENDTIME_COL = "EndTime";
    private static final String REASON_COL = "Reason";
    public static final String ACKNOWLEDGED_COL = "Acknowledged";

    public static final String TABLE_NAME = "Absence";
    public static final String[] PRIMARY_KEY = {USERNR_COL, ABSENCEID_COL};
    public static final String[] ALL_COLUMNS = {ABSENCEID_COL, USERNR_COL, ABSENCETYPEID_COL, STARTTIME_COL, ENDTIME_COL, REASON_COL, ACKNOWLEDGED_COL};

    protected Absence_JDBCDAO() {
        super(TABLE_NAME, ALL_COLUMNS, PRIMARY_KEY);
    }

    @Override
    protected Map<String, Object> entityToMap(Absence entity) {
        Map<String, Object> resMap = new HashMap<>();

        resMap.put(ABSENCEID_COL, entity.getAbsenceID());
        resMap.put(USERNR_COL, entity.getUser().getUserNr());
        resMap.put(ABSENCETYPEID_COL, entity.getAbsenceType().getAbsenceTypeID());
        resMap.put(STARTTIME_COL, entity.getStartTime());
        resMap.put(ENDTIME_COL, entity.getEndTime());
        resMap.put(REASON_COL, entity.getReason());
        resMap.put(ACKNOWLEDGED_COL, entity.isAcknowledged());

        return resMap;
    }

    @Override
    protected Absence getEntityFromResultSet(ResultSet rs) {
        try {
            return new Absence(rs.getInt(ABSENCEID_COL), new User_JDBCDAO().getUser(rs.getInt(USERNR_COL)), new AbsenceType_JDBCDAO().getAbsenceTypeByID(rs.getInt(ABSENCETYPEID_COL)), rs.getTimestamp(STARTTIME_COL).toLocalDateTime(), rs.getTimestamp(ENDTIME_COL).toLocalDateTime(), rs.getString(REASON_COL), rs.getBoolean(ACKNOWLEDGED_COL));
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
    public List<Absence> getAbsencesByAbsenceType(AbsenceType at) {
        List<Absence> absences = new ArrayList<>();

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ABSENCETYPEID_COL + " = " + at.getAbsenceTypeID() + " " + SQL_ORDER_BY_LINE)) {

            while (rs.next()) {
                absences.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return absences;
    }

    @Override
    public List<Absence> getAbsencesByAbsenceTypeAndUser(AbsenceType at, User u) {
        List<Absence> absences = new ArrayList<>();

        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ABSENCETYPEID_COL + " = " + at.getAbsenceTypeID() + " AND " + USERNR_COL + " = " + u.getUserNr() + " " + SQL_ORDER_BY_LINE)) {

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
    public List<Absence> getAbsencesByUserAndAcknowledgment(User u, boolean acknowledged) {
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
        Date startDate = new Date(startDateU.getTime());
        Date endDate = new Date(endDateU.getTime());
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + STARTTIME_COL + " >= '" + startDate + "' AND " + STARTTIME_COL + " < '" + endDate + "' OR "
                        + ENDTIME_COL + " >= '" + startDate + "' AND " + ENDTIME_COL + " < '" + endDate + "' OR " + STARTTIME_COL + " < '" + startDate + "' AND " + ENDTIME_COL + " >= '" + endDate + "' " + SQL_ORDER_BY_LINE)) {

            while (rs.next()) {
                absences.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return absences;
    }

    @Override
    public List<Absence> getAbsencesByUserBetweenDates(User user, java.util.Date startDateU, java.util.Date endDateU) {
        List<Absence> absences = new ArrayList<>();
        Date startDate = new Date(startDateU.getTime());
        Date endDate = new Date(endDateU.getTime());
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERNR_COL + " = " + user.getUserNr() + " AND (" + STARTTIME_COL + " >= '" + startDate + "' AND " + STARTTIME_COL + " < '" + endDate + "' OR "
                        + ENDTIME_COL + " >= '" + startDate + "' AND " + ENDTIME_COL + " < '" + endDate + "' OR " + STARTTIME_COL + " < '" + startDate + "' AND " + ENDTIME_COL + " >= '" + endDate + "') " + SQL_ORDER_BY_LINE)) {

            while (rs.next()) {
                absences.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return absences;
    }
}
