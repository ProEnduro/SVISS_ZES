/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.User;
import java.sql.Connection;
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
public class Absence_JDBCDAO extends Base_JDBCDAO<Absence> implements Absence_DAO {

    public static final String ABSENCEID_COL = "AbsenceID";
    public static final String USERNR_COL = User_JDBCDAO.USERNR_COL;
    public static final String ABSENCETYPEID_COL = "AbsenceTypeID";
    public static final String STARTTIME_COL = "StartTime";
    public static final String ENDTIME_COL = "EndTime";
    public static final String ACKNOWLEDGED_COL = "Acknowledged";

    public static final String TABLE_NAME = "Absence";
    public static final String[] PRIMARY_KEY = {ABSENCEID_COL, USERNR_COL};
    public static final String[] ALL_COLUMNS = {ABSENCEID_COL, USERNR_COL, ABSENCETYPEID_COL, STARTTIME_COL, ENDTIME_COL, ACKNOWLEDGED_COL};

    protected Absence_JDBCDAO() {
        super(TABLE_NAME, ALL_COLUMNS, PRIMARY_KEY);
    }

    @Override
    protected Map<String, Object> entityToMap(Absence entity) {
        Map<String, Object> resMap = new HashMap<>();

        resMap.put(ABSENCEID_COL, entity.getAbsenceID());
        resMap.put(USERNR_COL, entity.getUser().getUserNr());
        resMap.put(ABSENCETYPEID_COL, entity.getAbsenceType().getAbsenceID());
        resMap.put(STARTTIME_COL, entity.getStartTime());
        resMap.put(ENDTIME_COL, entity.getEndTime());
        resMap.put(ACKNOWLEDGED_COL, entity.isAcknowledged());

        return resMap;
    }

    @Override
    protected Absence getEntityFromResultSet(ResultSet rs) {
        try {
            return new Absence(rs.getInt(ABSENCEID_COL), new User_JDBCDAO().getUser(rs.getInt(USERNR_COL)), new AbsenceType_JDBCDAO().getAbsenceTypeByID(rs.getInt(ABSENCETYPEID_COL)), rs.getDate(STARTTIME_COL), rs.getDate(ENDTIME_COL), rs.getBoolean(ACKNOWLEDGED_COL));
        } catch (SQLException ex) {
            Logger.getLogger(Absence_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected void updateEntityWithAutoKeys(ResultSet rs, Absence entity) {
        try {
            entity.setAbsenceID(rs.getInt(ABSENCEID_COL));
        } catch (SQLException ex) {
            Logger.getLogger(Absence_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<Absence> getAbsenceByUser(User u) {
        List<Absence> absences = new ArrayList<>();

        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERNR_COL + " = " + u.getUserNr())) {

            while (rs.next()) {
                absences.add(new Absence(rs.getInt(ABSENCEID_COL), new User_JDBCDAO().getUser(rs.getInt(USERNR_COL)), new AbsenceType_JDBCDAO().getAbsenceTypeByID(rs.getInt(ABSENCETYPEID_COL)), rs.getDate(STARTTIME_COL), rs.getDate(ENDTIME_COL), rs.getBoolean(ACKNOWLEDGED_COL)));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return absences;
    }
}
