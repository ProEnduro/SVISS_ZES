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

    private final static String GET_ALLUSER_STATEMENT = "SELECT * FROM user";
    private SimpleDateFormat sdf = new SimpleDateFormat("DD.mm.YYYY");

    @Override
    public List<User> getUserList() {
        List<User> userList = new ArrayList<>();

        //use try-with-resources for best practice
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(GET_ALLUSER_STATEMENT)) {

            while (rs.next()) {
                userList.add(new User(rs.getInt("PersNr"), rs.getInt("JobID"), rs.getInt("AccessLevelID"), rs.getString("PersName"), rs.getInt("VacationLeft"), rs.getInt("OvertimeLeft"), rs.getString("Username"), rs.getString("E-Mail"), sdf.parse(rs.getString("Hiredate")), rs.getString("Password")));
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
            stmt.executeUpdate("insert into user(PersNr, JobID, AccessLevelID,PersName,VacationLeft,OvertimeLeft,Username,EMail,Hiredate,Password) values(" + u.getPersnr() + ", " + u.getJobId() + ", " + u.getAccessLevelId()
                    + ", " + u.getPersName() + ", " + u.getVacationLeft() + ", " + u.getOverTimeLeft() + ", " + u.getUsername() + ", " + u.getEmail() + ", " + u.getHiredate() + ", " + u.getPass() + ");");
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void updateUser(User u) {
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();) {
            stmt.executeUpdate("update user set PersNr=" + u.getPersnr() + ",  JobID=" + u.getJobId() + ",  AccessLevelID=" + u.getAccessLevelId() + ", PersName= " + u.getPersName() + ",  VacationLeft=" + u.getVacationLeft()
                    + ",  OvertimeLeft=" + u.getOverTimeLeft() + ",  Username=" + u.getUsername() + ", EMail= " + u.getEmail() + ",  Hiredate=" + u.getHiredate() + ",  Password=" + u.getPass() + " where persnr=" + u.getPersnr() + ";");
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
                u = new User(rs.getInt("PersNr"), rs.getInt("JobID"), rs.getInt("AccessLevelID"), rs.getString("PersName"), rs.getInt("VacationLeft"), rs.getInt("OvertimeLeft"), rs.getString("Username"), rs.getString("E-Mail"), sdf.parse(rs.getString("Hiredate")), rs.getString("Password"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }
}
