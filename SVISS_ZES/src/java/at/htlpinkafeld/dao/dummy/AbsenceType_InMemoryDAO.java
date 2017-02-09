/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.AbsenceType_DAO;
import at.htlpinkafeld.pojo.AbsenceType;
import java.util.LinkedList;

/**
 *
 * @author Martin Six
 */
public class AbsenceType_InMemoryDAO extends Base_InMemoryDAO<AbsenceType> implements AbsenceType_DAO {

    protected AbsenceType_InMemoryDAO() {
        super(new LinkedList<>());
        super.insert(new AbsenceType(1, AbsenceType_DAO.MEDICAL_LEAVE));
        super.insert(new AbsenceType(2, AbsenceType_DAO.HOLIDAY));
        super.insert(new AbsenceType(3, AbsenceType_DAO.TIMECOMPENSATION));
        super.insert(new AbsenceType(4, AbsenceType_DAO.BUSINESSRELATED_ABSENCE));
    }

    @Override
    public AbsenceType getAbsenceTypeByID(int absenceTypeId) {
        for (AbsenceType at : super.getList()) {
            if (at.getAbsenceTypeID() == absenceTypeId) {
                return clone(at);
            }
        }
        return null;
    }

    @Override
    protected AbsenceType clone(AbsenceType entity) {
        return new AbsenceType(entity);
    }

    @Override
    protected void setID(AbsenceType entity, int id) {
        entity.setAbsenceTypeID(id);
    }
}
