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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
        List<SollZeit> sollZeiten = SOLL_ZEITEN__DAO.getSollZeitenByUser(u);
        List<SollZeit> sollZeiten_ohneNull = new ArrayList<>();
        for (SollZeit sz : sollZeiten) {
            if (!sz.getSollStartTime().equals(sz.getSollEndTime())) {
                sollZeiten_ohneNull.add(sz);
            }
        }
        return sollZeiten_ohneNull;
    }

//    public static List<SollZeit> getSollZeiten() {
//        return SOLL_ZEITEN__DAO.getList();
//    }
    public static void insertZeit(SollZeit o) {
        SOLL_ZEITEN__DAO.insert(o);
    }

    public static void deleteZeit(SollZeit o) {
        o.setSollStartTime(LocalTime.MIN);
        o.setSollEndTime(LocalTime.MIN);
        SOLL_ZEITEN__DAO.insert(o);
    }

    public static SollZeit getSollZeitenByUser_DayOfWeek_ValidDate(User u, DayOfWeek d, LocalDateTime ldt) {
        SollZeit sz = SOLL_ZEITEN__DAO.getSollZeitenByUser_DayOfWeek_ValidDate(u, d, ldt);
        if (sz==null||sz.getSollStartTime().equals(sz.getSollEndTime())) {
            return null;
        } else {
            return sz;
        }
    }
    
    public static SollZeit getSollZeitByUserAndDayOfWeek(User u, DayOfWeek dow){
        return SOLL_ZEITEN__DAO.getSollZeitenByUser_DayOfWeek(u, dow);
    }

//    public static double getSollZeitForToday(User current) {
//        List<SollZeit> list = SOLL_ZEITEN__DAO.getSollZeitenByUser(current);
//        double sollzeitToday = 0;
//
//        LocalDate date = LocalDate.now();
//        DayOfWeek dayofweek = date.getDayOfWeek();
//
//        LocalTime start;
//        LocalTime end;
//
//        for (SollZeit s : list) {
//            if (s.getDay().equals(dayofweek)) {
//                start = s.getSollStartTime();
//                end = s.getSollEndTime();
//
//                sollzeitToday = start.until(end, ChronoUnit.MINUTES);
//                return sollzeitToday;
//            }
//        }
//        return -1;
//    }
}
