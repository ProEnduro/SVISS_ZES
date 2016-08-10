/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.Holiday_DAO;
import at.htlpinkafeld.dao.util.DAOException;
import at.htlpinkafeld.pojo.Holiday;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class HolidayService {

    private static final Holiday_DAO HOLIDAY_DAO;

    static {
        HOLIDAY_DAO = DAOFactory.getDAOFactory().getHolidayDAO();
    }

    public static List<Holiday> getHolidayBetweenDates(Date startDate, Date endDate) {
        return HOLIDAY_DAO.getHolidayBetweenDates(startDate, endDate);
    }

    public static List<Holiday> getList() {
        return HOLIDAY_DAO.getList();
    }

    public static void insert(Holiday o) throws DAOException {
        HOLIDAY_DAO.insert(o);
    }

    public static void delete(Holiday o) throws DAOException {
        HOLIDAY_DAO.delete(o);
    }

    
}
