/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao;

import at.htlpinkafeld.pojo.User;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Six
 */
public class User_JDBCDAO implements User_DAO {

    private static final String TABLE_NAME = "User";
    private static final String USERNR_COL = "USERNR";
    private static final String ACCESSLEVELID_COL = "ACCESSLEVELID";
    private static final String PERSNAME_COL = "USERNAME";
    private static final String VACATIONLEFT_COL = "VACATIONLEFT";
    private static final String OVERTIMELEFT_COL = "OVERTIMELEFT";
    private static final String USERNAME_COL = "USERNAME";
    private static final String EMAIL_COL = "EMAIL";
    private static final String HIREDATE_COL = "HIREDATE";
    private static final String PASSWORD_COL = "PASSWORD";
    private static final String WEEKTIME_COL = "WEEKTIME";

    private final static String GET_ALLUSER_STATEMENT = "SELECT * FROM " + TABLE_NAME;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public List<User> getList() {
        List<User> userList = new ArrayList<>();

        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(GET_ALLUSER_STATEMENT)) {

            AccessLevel_DAO al_DAO = new AccessLevel_JDBCDAO();
            while (rs.next()) {
                userList.add(new User(rs.getInt(USERNR_COL), al_DAO.getAccessLevelByID(rs.getInt(ACCESSLEVELID_COL)), rs.getString(PERSNAME_COL), rs.getInt(VACATIONLEFT_COL), rs.getInt(OVERTIMELEFT_COL), rs.getString(USERNAME_COL),
                        rs.getString(EMAIL_COL), sdf.parse(rs.getString(HIREDATE_COL)), rs.getString(PASSWORD_COL), rs.getDouble(WEEKTIME_COL)));
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return userList;
    }

    @Override
    public void insert(User u) {
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();) {
            stmt.executeUpdate("insert into " + TABLE_NAME + "(" + USERNR_COL + ", " + ACCESSLEVELID_COL + "," + PERSNAME_COL + "," + VACATIONLEFT_COL + "," + OVERTIMELEFT_COL + "," + USERNAME_COL + ","
                    + EMAIL_COL + "," + HIREDATE_COL + "," + PASSWORD_COL + "," + WEEKTIME_COL + ") values(" + u.getPersNr() + ", " + u.getAccessLevel().getAccessLevelID() + ", " + u.getPersName() + ", " + u.getVacationLeft() + ", "
                    + u.getOverTimeLeft() + ", " + u.getUsername() + ", " + u.getEmail() + ", " + u.getHiredate() + ", " + u.getPass() + ", " + u.getWeekTime() + ");"
            );
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(User u) {
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();) {
            stmt.executeUpdate("update " + TABLE_NAME + " set " + ACCESSLEVELID_COL + "=" + u.getAccessLevel().getAccessLevelID() + ", " + PERSNAME_COL + "= " + u.getPersName() + ",  " + VACATIONLEFT_COL + "=" + u.getVacationLeft()
                    + ",  " + OVERTIMELEFT_COL + "=" + u.getOverTimeLeft() + ",  " + USERNAME_COL + "=" + u.getUsername() + ", " + EMAIL_COL + "= " + u.getEmail() + ",  " + HIREDATE_COL + "=" + u.getHiredate() + ",  "
                    + PASSWORD_COL + "=" + u.getPass() + ",  " + WEEKTIME_COL + "=" + u.getWeekTime() + " where " + USERNR_COL + "=" + u.getPersNr() + ";");
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(User u) {
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();) {
            stmt.executeUpdate("delete from  " + TABLE_NAME + "  where persnr=" + u.getPersNr() + ";");
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public User getUser(int usernr) {
        User u = null;
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(GET_ALLUSER_STATEMENT + "WHERE " + USERNAME_COL + " = " + usernr)) {

            AccessLevel_DAO al_DAO = new AccessLevel_JDBCDAO();
            if (rs.next()) {
                u = new User(rs.getInt(USERNR_COL), al_DAO.getAccessLevelByID(rs.getInt(ACCESSLEVELID_COL)), rs.getString(PERSNAME_COL), rs.getInt(VACATIONLEFT_COL), rs.getInt(OVERTIMELEFT_COL), rs.getString(USERNAME_COL), rs.getString(EMAIL_COL), sdf.parse(rs.getString(HIREDATE_COL)), rs.getString(PASSWORD_COL), rs.getDouble(WEEKTIME_COL));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }
}
