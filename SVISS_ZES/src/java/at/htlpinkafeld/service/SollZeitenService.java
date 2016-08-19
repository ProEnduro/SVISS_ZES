/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class SollZeitenService {

    private static final SollZeiten_DAO SOLL_ZEITEN__DAO;

    static {
        SOLL_ZEITEN__DAO = DAOFactory.getDAOFactory().getSollZeitenDAO();
    }

    public static List<SollZeit> getSollZeitenByUser(User u) {
        return SOLL_ZEITEN__DAO.getSollZeitenByUser(u);
    }

    public static List<SollZeit> getSollZeiten() {
        return SOLL_ZEITEN__DAO.getList();
    }

    public static void insertZeit(SollZeit o) {
        SOLL_ZEITEN__DAO.insert(o);
    }

    public static void updateZeit(SollZeit o) {
        SOLL_ZEITEN__DAO.update(o);
    }

    public static void deleteZeit(SollZeit o) {
        SOLL_ZEITEN__DAO.delete(o);
    }

    public static double getSollZeitForToday(User current) {
        List<SollZeit> list = SOLL_ZEITEN__DAO.getSollZeitenByUser(current);
        double sollzeitToday = 0;

        LocalDate date = LocalDate.now();
        DayOfWeek dayofweek = date.getDayOfWeek();

        LocalTime start;
        LocalTime end;

        for (SollZeit s : list) {
            if (s.getDay().equals(dayofweek)) {
                start = s.getSollStartTime();
                end = s.getSollEndTime();

                sollzeitToday = start.until(end, ChronoUnit.MINUTES);
                return sollzeitToday;
            }
        }
        return -1;
    }
    
    public static SollZeit getSollZeitByUserAndDayOfWeek(User u, DayOfWeek dow){
        return SOLL_ZEITEN__DAO.getSollZeitenByUser_DayOfWeek(u, dow);
    }

}
