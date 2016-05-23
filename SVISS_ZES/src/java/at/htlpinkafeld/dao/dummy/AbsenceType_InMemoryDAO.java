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
        super.insert(new AbsenceType(1, "medical leave"));
        super.insert(new AbsenceType(2, "holiday"));
        super.insert(new AbsenceType(3, "time compensation"));
        super.insert(new AbsenceType(4, "business-related absence"));
    }

    @Override
    public AbsenceType getAbsenceTypeByID(int absenceTypeId) {
        for (AbsenceType at : super.getList()) {
            if (at.getAbsenceID() == absenceTypeId) {
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
        entity.setAbsenceID(id);
    }
}
