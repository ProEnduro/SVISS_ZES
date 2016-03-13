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
public class User_JDBCDAO extends User_DAO {

    private final static String GET_ALLUSER_STATEMENT = "SELECT * FROM user";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public List<User> getUserList() {
        List<User> userList = new ArrayList<>();

        //use try-with-resources for best practice
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(GET_ALLUSER_STATEMENT)) {

            while (rs.next()) {
                userList.add(new User(rs.getInt(PERSNR), rs.getInt(ACCESSLEVELID), rs.getString(PERSNAME), rs.getInt(VACATIONLEFT), rs.getInt(OVERTIMELEFT), rs.getString(USERNAME), rs.getString(EMAIL), sdf.parse(rs.getString(HIREDATE)), rs.getString(PASSWORD), rs.getDouble(WEEKTIME)));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return userList;
    }

    @Override
    public void insertUser(User u) {
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();) {
            stmt.executeUpdate("insert into user(" + PERSNR + ", " + ACCESSLEVELID + "," + PERSNAME + "," + VACATIONLEFT + "," + OVERTIMELEFT + "," + USERNAME + "," + EMAIL + "," + HIREDATE + "," + PASSWORD + "," + WEEKTIME + ") values(" + u.getPersnr() + ", " + u.getAccessLevelId()
                    + ", " + u.getPersName() + ", " + u.getVacationLeft() + ", " + u.getOverTimeLeft() + ", " + u.getUsername() + ", " + u.getEmail() + ", " + u.getHiredate() + ", " + u.getPass() + ", " + u.getWeekTime() + ");"
            );
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void updateUser(User u) {
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();) {
            stmt.executeUpdate("update user set " + ACCESSLEVELID + "=" + u.getAccessLevelId() + ", " + PERSNAME + "= " + u.getPersName() + ",  " + VACATIONLEFT + "=" + u.getVacationLeft()
                    + ",  " + OVERTIMELEFT + "=" + u.getOverTimeLeft() + ",  " + USERNAME + "=" + u.getUsername() + ", " + EMAIL + "= " + u.getEmail() + ",  " + HIREDATE + "=" + u.getHiredate() + ",  " + PASSWORD + "=" + u.getPass() + ",  " + WEEKTIME + "=" + u.getWeekTime() + " where " + PERSNR + "=" + u.getPersnr() + ";");
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void deleteUser(int persnr) {
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();) {
            stmt.executeUpdate("delete from  user  where persnr=" + persnr + ";");
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public User getUser(int persnr) {
        User u = null;
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(GET_ALLUSER_STATEMENT)) {

            if (rs.next()) {
                u = new User(rs.getInt(PERSNR), rs.getInt(ACCESSLEVELID), rs.getString(PERSNAME), rs.getInt(VACATIONLEFT), rs.getInt(OVERTIMELEFT), rs.getString(USERNAME), rs.getString(EMAIL), sdf.parse(rs.getString(HIREDATE)), rs.getString(PASSWORD), rs.getDouble(WEEKTIME));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }
}
