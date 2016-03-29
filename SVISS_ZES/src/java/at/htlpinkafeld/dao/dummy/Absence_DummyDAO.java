/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.Absence_DAO;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.User;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class Absence_DummyDAO extends Base_DummyDAO<Absence> implements Absence_DAO {

    public Absence_DummyDAO() {
        super(new ArrayList<>());
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
