/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.AbsenceType_DAO;
import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.AbsenceType;
import at.htlpinkafeld.pojo.User;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class Absence_InMemoryDAO extends Base_InMemoryDAO<Absence> implements Absence_DAO {

    protected Absence_InMemoryDAO() {
        super(new ArrayList<>());
        User_DAO uDAO = new User_InMemoryDAO();
        AbsenceType_DAO aDAO = new AbsenceType_InMemoryDAO();
        super.insert(new Absence(1, uDAO.getUser(1), aDAO.getAbsenceTypeByID(1), new GregorianCalendar(2016, 4, 4).getTime(), new GregorianCalendar(2016, 4, 6).getTime(), "Cold", true));
        super.insert(new Absence(uDAO.getUser(2), aDAO.getAbsenceTypeByID(3), new GregorianCalendar(2016, 4, 4, 14, 0).getTime(), new GregorianCalendar(2016, 4, 4, 18, 0).getTime()));
    }

    @Override
    public List<Absence> getAbsenceByUser(User u) {
        List<Absence> aList = new LinkedList<>();
        for (Absence a : super.getList()) {
            if (a.getUser().equals(u)) {
                aList.add(clone(a));
            }
        }
        return aList;
    }

    @Override
    public List<Absence> getAbsenceByAbsenceType(AbsenceType at) {
        List<Absence> aList = new LinkedList<>();
        for (Absence a : super.getList()) {
            if (a.getAbsenceType().equals(at)) {
                aList.add(clone(a));
            }
        }
        return aList;
    }

    @Override
    public List<Absence> getAbsenceByAbsenceTypeAndUser(AbsenceType at, User u) {
        List<Absence> aList = new LinkedList<>();
        for (Absence a : super.getList()) {
            if (a.getAbsenceType().equals(at) && a.getUser().equals(u)) {
                aList.add(clone(a));
            }
        }
        return aList;
    }

    @Override
    protected Absence clone(Absence entity
    ) {
        return new Absence(entity);
    }

    @Override
    protected void setID(Absence entity, int id
    ) {
        entity.setAbsenceID(id);
    }
}