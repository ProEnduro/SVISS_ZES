/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.AbsenceType_DAO;
import at.htlpinkafeld.pojo.AbsenceType;
import java.util.LinkedList;

/**
 *
 * @author Martin Six
 */
public class AbsenceType_DummyDAO extends Base_DummyDAO<AbsenceType> implements AbsenceType_DAO {

    public AbsenceType_DummyDAO() {
        super(new LinkedList<>());
    }
}
