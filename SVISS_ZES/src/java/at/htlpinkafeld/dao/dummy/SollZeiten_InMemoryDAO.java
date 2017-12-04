/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.dao.util.DAOException;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Martin Six
 */
public class SollZeiten_InMemoryDAO extends Base_InMemoryDAO<SollZeit> implements SollZeiten_DAO {

    protected SollZeiten_InMemoryDAO() {
        super(new ArrayList<>());
        User_DAO uDAO = new User_InMemoryDAO();

        Map<DayOfWeek, LocalTime> startTimeMap = new HashMap<>();
        Map<DayOfWeek, LocalTime> endTimeMap = new HashMap<>();

        for (int i = 1; i < 5; i++) {
            startTimeMap.put(DayOfWeek.of(i), LocalTime.parse("08:00:00"));
            endTimeMap.put(DayOfWeek.of(i), LocalTime.parse("16:30:00"));
        }

        super.insert(new SollZeit(uDAO.getUser(1), startTimeMap, endTimeMap));
        super.insert(new SollZeit(uDAO.getUser(2), startTimeMap, endTimeMap));
    }

    @Override
    public void insert(SollZeit o) {
        try {
            super.delete(o);
            super.insert(o);
        } catch (DAOException ex) {
        }
    }

    @Override
    public void delete(SollZeit o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(SollZeit o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SollZeit> getSollZeitenByUser(User u) {
        List<SollZeit> szList = new LinkedList<>();
        super.getList().stream().filter((sz) -> (sz.getUser().equals(u))).forEachOrdered((sz) -> {
            szList.add(clone(sz));
        });
        return szList;
    }

    @Override
    public SollZeit getSollZeitenByUser_Current(User u) {
        for (SollZeit sz : super.getList()) {
            if (sz.getUser().equals(u)) {
                return sz;
            }
        }
        return null;
    }

    @Override
    public SollZeit getSollZeitenByUser_ValidDate(User u, LocalDateTime ldt) {
        for (SollZeit sz : super.getList()) {
            if (sz.getUser().equals(u)) {
                return sz;
            }
        }
        return null;
    }

    @Override
    protected SollZeit clone(SollZeit entity) {
        SollZeit sz = new SollZeit(entity);
        sz.setSollZeitID(entity.getSollZeitID());
        return sz;
    }

    @Override
    protected void setID(SollZeit entity, int id) {
        entity.setSollZeitID(id);
    }

    @Override
    public void deleteAllSollZeitenFromUser(User o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
