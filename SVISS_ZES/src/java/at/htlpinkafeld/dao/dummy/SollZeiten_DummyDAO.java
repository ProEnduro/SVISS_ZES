/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.SollZeiten;
import at.htlpinkafeld.pojo.User;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class SollZeiten_DummyDAO extends Base_DummyDAO<SollZeiten>implements SollZeiten_DAO {


    public SollZeiten_DummyDAO() {
        super(new ArrayList<>());
        User_DAO uDAO = new User_DummyDAO();
        for (int i = 1; i < 5; i++) {
            super.insert(new SollZeiten(DayOfWeek.of(i), uDAO.getUser(1), LocalTime.parse("8:0:0"), LocalTime.parse("16:30:0")));
        }
        super.insert(new SollZeiten(DayOfWeek.FRIDAY, uDAO.getUser(1), LocalTime.parse("8:0:0"), LocalTime.parse("12:0:0")));

        for (int i = 1; i < 5; i++) {
            super.insert(new SollZeiten(DayOfWeek.of(i), uDAO.getUser(2), LocalTime.parse("8:0:0"), LocalTime.parse("16:30:0")));
        }
        super.insert(new SollZeiten(DayOfWeek.FRIDAY, uDAO.getUser(2), LocalTime.parse("8:0:0"), LocalTime.parse("12:0:0")));
    }

    @Override
    public List<SollZeiten> getSollZeitenByUser(User u) {
        List<SollZeiten> szList = new LinkedList<>();
        for (SollZeiten sz : super.getList()) {
            if (sz.getUser().equals(u)) {
                szList.add(sz);
            }
        }
        return szList;
    }

}
