/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.AccessLevel_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.dao.util.DAOException;
import at.htlpinkafeld.dao.util.WrappedConnection;
import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserProxy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
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
public class User_JDBCDAO extends Base_JDBCDAO<User> implements User_DAO {

    public static final String USERNR_COL = "USERNR";
    public static final String ACCESSLEVELID_COL = "ACCESSLEVELID";
    public static final String PERSNAME_COL = "PERSNAME";
    public static final String VACATIONLEFT_COL = "VACATIONLEFT";
    public static final String OVERTIMELEFT_COL = "OVERTIMELEFT";
    public static final String USERNAME_COL = "USERNAME";
    public static final String EMAIL_COL = "EMAIL";
    public static final String HIREDATE_COL = "HIREDATE";
    public static final String PASSWORD_COL = "PASSWORD";
    public static final String WEEKTIME_COL = "WEEKTIME";
    public static final String DISABLED_COL = "DISABLED";

    public static final String REL_USERNR_COL = USERNR_COL;
    public static final String REL_APPROVER_COL = "APPROVERNR";

    public static final String TABLE_NAME = "User";
    public static final String RELATION_TABLE_NAME = "ApproverUser";
    public static final String PRIMARY_KEY = USERNR_COL;
    private static final String[] ALL_COLUMNS = {USERNR_COL, ACCESSLEVELID_COL, PERSNAME_COL, VACATIONLEFT_COL, OVERTIMELEFT_COL, USERNAME_COL, EMAIL_COL,
        HIREDATE_COL, PASSWORD_COL, WEEKTIME_COL, DISABLED_COL};

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    protected User_JDBCDAO() {
        super(TABLE_NAME, ALL_COLUMNS, PRIMARY_KEY);
    }

    @Override
    public void insert(User u) throws DAOException {
        super.insert(u);
        if (u.areApproverInitialized()) {
            try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                    Statement stmt = con.getConn().createStatement()) {
                for (User approver : u.getApprover()) {
                    stmt.executeUpdate("INSERT INTO " + RELATION_TABLE_NAME + " ( " + REL_APPROVER_COL + ", " + REL_USERNR_COL + " ) VALUES (" + approver.getUserNr() + ", " + u.getUserNr() + ");");
                }
            } catch (SQLException ex) {
                Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void updateApproverOfUser(User user) {
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement()) {
            stmt.executeUpdate("DELETE FROM " + RELATION_TABLE_NAME + " WHERE " + REL_USERNR_COL + " = " + user.getUserNr() + ";");
            for (User approver : user.getApprover()) {
                stmt.addBatch("INSERT INTO " + RELATION_TABLE_NAME + " ( " + REL_APPROVER_COL + ", " + REL_USERNR_COL + " ) VALUES (" + approver.getUserNr() + ", " + user.getUserNr() + ");");
            }
            stmt.executeBatch();
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void removeApprover(User approver) {
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement()) {
            stmt.executeUpdate("DELETE FROM " + RELATION_TABLE_NAME + " WHERE " + REL_APPROVER_COL + " = " + approver.getUserNr() + ";");
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public User getUser(int usernr) {
        User u = null;
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERNR_COL + " = " + usernr + " " + SQL_ORDER_BY_LINE)) {

            if (rs.next()) {
                u = getEntityFromResultSet(rs);

            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }

    @Override
    public User getUserByUsername(String username) {
        User u = null;
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME_COL + " = '" + username + "' " + SQL_ORDER_BY_LINE)) {

            if (rs.next()) {
                u = getEntityFromResultSet(rs);
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }

    @Override
    public User getUserByEmail(String email) {
        User u = null;
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + EMAIL_COL + " = '" + email + "' " + SQL_ORDER_BY_LINE)) {

            if (rs.next()) {
                u = getEntityFromResultSet(rs);
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }

    @Override
    public List<User> getUserByDisabled(Boolean disabled) {
        List<User> userL = new ArrayList<>();
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + DISABLED_COL + " IS " + disabled + " " + SQL_ORDER_BY_LINE)) {

            while (rs.next()) {
                userL.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userL;
    }

    @Override
    public List<User> getApprover(User user) {
        List<User> userL = new ArrayList<>();
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME + " JOIN " + RELATION_TABLE_NAME
                        + " ON( " + TABLE_NAME + "." + USERNR_COL + " = " + RELATION_TABLE_NAME + "." + REL_APPROVER_COL + ") "
                        + "WHERE " + RELATION_TABLE_NAME + "." + REL_USERNR_COL + " = " + user.getUserNr() + " " + SQL_ORDER_BY_LINE)) {

            while (rs.next()) {
                userL.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userL;
    }

    @Override
    public List<User> getUserByAccessLevel(AccessLevel accessLevel) {
        List<User> userL = new ArrayList<>();
        try (WrappedConnection con = ConnectionManager.getInstance().getWrappedConnection();
                Statement stmt = con.getConn().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ACCESSLEVELID_COL + " = " + accessLevel.getAccessLevelID() + " " + SQL_ORDER_BY_LINE)) {

            while (rs.next()) {
                userL.add(getEntityFromResultSet(rs));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userL;
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
        resMap.put(DISABLED_COL, entity.isDisabled());

        return resMap;
    }

    @Override
    protected User getEntityFromResultSet(ResultSet rs) {
        User u = new UserProxy();
        try {
            u.setUserNr(rs.getInt(USERNR_COL));
            AccessLevel_DAO alDAO = new AccessLevel_JDBCDAO();
            u.setAccessLevel(alDAO.getAccessLevelByID(rs.getInt(ACCESSLEVELID_COL)));
            u.setPersName(rs.getString(PERSNAME_COL));
            u.setVacationLeft(rs.getInt(VACATIONLEFT_COL));
            u.setOverTimeLeft(rs.getInt(OVERTIMELEFT_COL));
            u.setUsername(rs.getString(USERNAME_COL));
            u.setEmail(rs.getString(EMAIL_COL));
            u.setHiredate(rs.getDate(HIREDATE_COL).toLocalDate());
            u.setPass(rs.getString(PASSWORD_COL));
            u.setWeekTime(rs.getDouble(WEEKTIME_COL));
            u.setDisabled(rs.getBoolean(DISABLED_COL));

        } catch (SQLException ex) {
            Logger.getLogger(Base_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }

    @Override
    protected void updateEntityWithAutoKeys(ResultSet rs, User entity) {
        try {
            rs.next();
            entity.setUserNr(rs.getInt(1));
        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
