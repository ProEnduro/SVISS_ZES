/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.pojo.SollZeiten;
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

    public static List<SollZeiten> getSollZeitenByUser(User u) {
        return SOLL_ZEITEN__DAO.getSollZeitenByUser(u);
    }

    public static List<SollZeiten> getSollZeiten() {
        return SOLL_ZEITEN__DAO.getList();
    }

    public static void insertZeit(SollZeiten o) {
        SOLL_ZEITEN__DAO.insert(o);
    }

    public static void updateZeit(SollZeiten o) {
        SOLL_ZEITEN__DAO.update(o);
    }

    public static void deleteZeit(SollZeiten o) {
        SOLL_ZEITEN__DAO.delete(o);
    }

    public static double getSollZeitForToday(User current) {
        List<SollZeiten> list = SOLL_ZEITEN__DAO.getSollZeitenByUser(current);
        double sollzeitToday = 0;

        LocalDate date = LocalDate.now();
        DayOfWeek dayofweek = date.getDayOfWeek();

        LocalTime start;
        LocalTime end;

        for (SollZeiten s : list) {
            if (s.getDay().equals(dayofweek)) {
                start = s.getSollStartTime();
                end = s.getSollEndTime();

                sollzeitToday = start.until(end, ChronoUnit.MINUTES);
                return sollzeitToday;
            }
        }
        return -1;
    }

}
