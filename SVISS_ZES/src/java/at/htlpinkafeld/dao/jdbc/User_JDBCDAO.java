/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.AccessLevel_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.User;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Six
 */
public class User_JDBCDAO extends Base_JDBCDAO<User> implements User_DAO {

    public static final String USERNR_COL = "USERNR";
    public static final String ACCESSLEVELID_COL = "ACCESSLEVELID";
    public static final String PERSNAME_COL = "USERNAME";
    public static final String VACATIONLEFT_COL = "VACATIONLEFT";
    public static final String OVERTIMELEFT_COL = "OVERTIMELEFT";
    public static final String USERNAME_COL = "USERNAME";
    public static final String EMAIL_COL = "EMAIL";
    public static final String HIREDATE_COL = "HIREDATE";
    public static final String PASSWORD_COL = "PASSWORD";
    public static final String WEEKTIME_COL = "WEEKTIME";

    public static final String TABLE_NAME = "User";
    public static final String PRIMARY_KEY = USERNR_COL;
    public static final String[] ALL_COLUMNS = {USERNR_COL, ACCESSLEVELID_COL, PERSNAME_COL, VACATIONLEFT_COL, OVERTIMELEFT_COL, USERNAME_COL, EMAIL_COL,
        HIREDATE_COL, PASSWORD_COL, WEEKTIME_COL};

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    protected User_JDBCDAO() {
        super(TABLE_NAME, ALL_COLUMNS, PRIMARY_KEY);
    }

    @Override
    public User getUser(int usernr) {
        User u = null;
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERNR_COL + " = " + usernr)) {

            AccessLevel_DAO al_DAO = new AccessLevel_JDBCDAO();
            if (rs.next()) {
                u = new User(rs.getInt(USERNR_COL), al_DAO.getAccessLevelByID(rs.getInt(ACCESSLEVELID_COL)), rs.getString(PERSNAME_COL), rs.getInt(VACATIONLEFT_COL), rs.getInt(OVERTIMELEFT_COL), rs.getString(USERNAME_COL), rs.getString(EMAIL_COL), sdf.parse(rs.getString(HIREDATE_COL)), rs.getString(PASSWORD_COL), rs.getDouble(WEEKTIME_COL));

            }

        } catch (SQLException | ParseException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }

    @Override
    public User getUserByUsername(String username) {
        User u = null;
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME_COL + " = " + username)) {

            AccessLevel_DAO al_DAO = new AccessLevel_JDBCDAO();
            if (rs.next()) {
                u = new User(rs.getInt(USERNR_COL), al_DAO.getAccessLevelByID(rs.getInt(ACCESSLEVELID_COL)), rs.getString(PERSNAME_COL), rs.getInt(VACATIONLEFT_COL), rs.getInt(OVERTIMELEFT_COL), rs.getString(USERNAME_COL), rs.getString(EMAIL_COL), sdf.parse(rs.getString(HIREDATE_COL)), rs.getString(PASSWORD_COL), rs.getDouble(WEEKTIME_COL));

            }

        } catch (SQLException | ParseException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }

    @Override
    protected Map<String, Object> entityToMap(User entity) {
        Map<String, Object> resMap = new HashMap<>();

        resMap.put(USERNR_COL, entity.getUserNr());
        resMap.put(ACCESSLEVELID_COL, entity.getAccessLevel().getAccessLevelID());
        resMap.put(PERSNAME_COL, entity.getPersName());
        resMap.put(VACATIONLEFT_COL, entity.getVacationLeft());
        resMap.put(OVERTIMELEFT_COL, entity.getOverTimeLeft());
        resMap.put(USERNAME_COL, entity.getUsername());
        resMap.put(EMAIL_COL, entity.getEmail());
        resMap.put(HIREDATE_COL, entity.getHiredate());
        resMap.put(PASSWORD_COL, entity.getPass());
        resMap.put(WEEKTIME_COL, entity.getWeekTime());

        return resMap;
    }

    @Override
    protected User getEntityFromResultSet(ResultSet rs) {
        User u = new User();
        try {
            u.setUserNr(rs.getInt(USERNR_COL));
            AccessLevel_DAO alDAO = new AccessLevel_JDBCDAO();
            u.setAccessLevel(alDAO.getAccessLevelByID(rs.getInt(ACCESSLEVELID_COL)));
            u.setPersName(rs.getString(PERSNAME_COL));
            u.setVacationLeft(rs.getInt(VACATIONLEFT_COL));
            u.setOverTimeLeft(rs.getInt(OVERTIMELEFT_COL));
            u.setUsername(rs.getString(USERNAME_COL));
            u.setEmail(rs.getString(EMAIL_COL));
            u.setHiredate(sdf.parse(rs.getString(HIREDATE_COL)));
            u.setPass(rs.getString(PASSWORD_COL));
            u.setWeekTime(rs.getDouble(WEEKTIME_COL));

        } catch (SQLException | ParseException ex) {
            Logger.getLogger(Base_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }

    @Override
    protected void updateEntityWithAutoKeys(ResultSet rs, User entity) {
        try {
            entity.setUserNr(rs.getInt(USERNR_COL));
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
