/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.AbsenceType_DAO;
import at.htlpinkafeld.dao.Absence_DAO;
import at.htlpinkafeld.dao.User_DAO;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.User;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class Absence_DummyDAO extends Base_DummyDAO<Absence> implements Absence_DAO {

    public Absence_DummyDAO() {
        super(new ArrayList<>());
        User_DAO uDAO = new User_DummyDAO();
        AbsenceType_DAO aDAO = new AbsenceType_DummyDAO();
        super.insert(new Absence(1, uDAO.getUser(1), aDAO.getAbsenceTypeByID(1), new GregorianCalendar(2016, 4, 4).getTime(), new GregorianCalendar(2016, 4, 6).getTime(), true));
        super.insert(new Absence(1, uDAO.getUser(2), aDAO.getAbsenceTypeByID(3), new GregorianCalendar(2016, 4, 4, 14, 0).getTime(), new GregorianCalendar(2016, 4, 4, 18, 0).getTime(), false));
    }

    @Override
    public List<Absence> getAbsenceByUser(User u) {
        List<Absence> aList = new LinkedList<>();
        for (Absence a : super.getList()) {
            if (a.getUser().equals(u)) {
                aList.add(a);
            }
        }
        return aList;
    }

}
