/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class SollZeiten_InMemoryDAO extends Base_InMemoryDAO<SollZeit> implements SollZeiten_DAO {

    protected SollZeiten_InMemoryDAO() {
        super(new ArrayList<>());
        User_DAO uDAO = new User_InMemoryDAO();
        for (int i = 1; i < 5; i++) {
            super.insert(new SollZeit(DayOfWeek.of(i), uDAO.getUser(1), LocalTime.parse("08:00:00"), LocalTime.parse("16:30:00")));
        }
        super.insert(new SollZeit(DayOfWeek.FRIDAY, uDAO.getUser(1), LocalTime.parse("08:00:00"), LocalTime.parse("12:00:00")));

        for (int i = 1; i < 5; i++) {
            super.insert(new SollZeit(DayOfWeek.of(i), uDAO.getUser(2), LocalTime.parse("08:00:00"), LocalTime.parse("16:30:00")));
        }
        super.insert(new SollZeit(DayOfWeek.FRIDAY, uDAO.getUser(2), LocalTime.parse("08:00:00"), LocalTime.parse("12:00:00")));
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
        for (SollZeit sz : super.getList()) {
            if (sz.getUser().equals(u)) {
                szList.add(clone(sz));
            }
        }
        return szList;
    }

    @Override
    public SollZeit getSollZeitenByUser_DayOfWeek(User u, DayOfWeek d) {
        for (SollZeit sz : super.getList()) {
            if (sz.getUser().equals(u)) {
                if (sz.getDay().equals(d)) {
                    return sz;
                }
            }
        }
        return null;
    }

    @Override
    public SollZeit getSollZeitenByUser_DayOfWeek_ValidDate(User u, DayOfWeek d, LocalDateTime ldt) {
        for (SollZeit sz : super.getList()) {
            if (sz.getUser().equals(u)) {
                if (sz.getDay().equals(d)) {
                    return sz;
                }
            }
        }
        return null;
    }

    @Override
    protected SollZeit clone(SollZeit entity) {
        return new SollZeit(entity);
    }

    @Override
    protected void setID(SollZeit entity, int id) {
    }

}
