/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.jdbc;

import at.htlpinkafeld.dao.interf.AbsenceType_DAO;
import at.htlpinkafeld.pojo.AbsenceType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Six
 */
public class AbsenceType_JDBCDAO extends Base_JDBCDAO<AbsenceType> implements AbsenceType_DAO {

    public static final String ABSENCETYPEID_COL = "AbsenceTypeID";
    public static final String ABSENCETYPENAME_COL = "AbsenceTypeName";

    public static final String TABLE_NAME = "AbsenceType";
    public static final String PRIMARY_KEY = ABSENCETYPEID_COL;
    public static final String[] ALL_COLUMNS = {ABSENCETYPEID_COL, ABSENCETYPENAME_COL};

    protected AbsenceType_JDBCDAO() {
        super(TABLE_NAME, ALL_COLUMNS, PRIMARY_KEY);
    }

    @Override
    protected Map<String, Object> entityToMap(AbsenceType entity) {
        Map<String, Object> resMap = new HashMap<>();

        resMap.put(ABSENCETYPEID_COL, entity.getAbsenceID());
        resMap.put(ABSENCETYPENAME_COL, entity.getAbsenceName());

        return resMap;
    }

    @Override
    protected AbsenceType getEntityFromResultSet(ResultSet rs) {
        try {
            return new AbsenceType(rs.getInt(ABSENCETYPEID_COL), rs.getString(ABSENCETYPENAME_COL));
        } catch (SQLException ex) {
            Logger.getLogger(AbsenceType_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    protected void updateEntityWithAutoKeys(ResultSet rs, AbsenceType entity) {
        try {
            entity.setAbsenceID(rs.getInt(ABSENCETYPEID_COL));
        } catch (SQLException ex) {
            Logger.getLogger(AbsenceType_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public AbsenceType getAbsenceTypeByID(int absenceTypeID) {
        AbsenceType at = null;
        try (Connection con = ConnectionManager.getInstance().getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ABSENCETYPEID_COL + " = " + absenceTypeID)) {

            if (rs.next()) {
                at = new AbsenceType(rs.getInt(ABSENCETYPEID_COL), rs.getString(ABSENCETYPENAME_COL));
            }

        } catch (SQLException ex) {
            Logger.getLogger(User_JDBCDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return at;
    }

}
