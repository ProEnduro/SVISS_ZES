/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.WorkTime_DAO;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
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
public class WorkTime_JDBCDAO extends Base_JDBCDAO<WorkTime> implements WorkTime_DAO {

    public static final String TIMEID_COL = "TimeID";
    public static final String USERNR_COL = User_JDBCDAO.USERNR_COL;
    public static final String STARTTIME_COL = "StartTime";
    public static final String ENDTIME_COL = "EndTime";
    public static final String BREAKTIME_COL = "BreakTime";
    public static final String STARTCOMMENT_COL = "StartComment";
    public static final String ENDCOMMENT_COL = "EndComment";

    public static final String TABLE_NAME = "WorkTime";
    public static final String[] PRIMARY_KEY = {TIMEID_COL, USERNR_COL};
    public static final String[] ALL_COLUMNS = {TIMEID_COL, USERNR_COL, STARTTIME_COL, ENDTIME_COL, BREAKTIME_COL, STARTCOMMENT_COL, ENDCOMMENT_COL};

    protected WorkTime_JDBCDAO() {
        super(TABLE_NAME, ALL_COLUMNS, PRIMARY_KEY);
    }

    @Override
    protected Map<String, Object> entityToMap(WorkTime entity) {
        Map<String, Object> resMap = new HashMap<>();

        resMap.put(TIMEID_COL, entity.getTimeID());
        resMap.put(USERNR_COL, entity.getUser().getUserNr());
        resMap.put(STARTTIME_COL, entity.getStartTime());
        resMap.put(ENDTIME_COL, entity.getEndTime());
        resMap.put(BREAKTIME_COL, entity.getBreakTime());
        resMap.put(STARTCOMMENT_COL, entity.getStartComment());
        resMap.put(ENDCOMMENT_COL, entity.getEndComment());

        return resMap;
    }

    @Override
    protected WorkTime getEntityFromResultSet(ResultSet rs) {
        try {
            return new WorkTime(rs.getInt(TIMEID_COL), new User_JDBCDAO().getUser(rs.getInt(USERNR_COL)), rs.getDate(STARTTIME_COL), rs.getDate(ENDTIME_COL), rs.getInt(BREAKTIME_COL), rs.getString(STARTCOMMENT_COL), rs.getString(ENDCOMMENT_COL));

        } catch (SQLException ex) {
            Logger.getLogger(WorkTime_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected void updateEntityWithAutoKeys(ResultSet rs, WorkTime entity) {
        try {
            entity.setTimeID(rs.getInt(TIMEID_COL));
        } catch (SQLException ex) {
            Logger.getLogger(WorkTime_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<WorkTime> getWorkTimesByUser(User u) {
        List<WorkTime> workTimes = new ArrayList<>();

        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERNR_COL + " = " + u.getUserNr())) {

            while (rs.next()) {
                workTimes.add(new WorkTime(rs.getInt(TIMEID_COL), u, rs.getDate(STARTTIME_COL), rs.getDate(ENDTIME_COL), rs.getInt(BREAKTIME_COL), rs.getString(STARTCOMMENT_COL), rs.getString(ENDCOMMENT_COL)));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return workTimes;
    }

}
