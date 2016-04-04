/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao;

import at.htlpinkafeld.pojo.AbsenceType;

/**
 *
 * @author Martin Six
 */
public interface AbsenceType_DAO extends Base_DAO<AbsenceType> {

    public abstract AbsenceType getAbsenceTypeByID(int accessLevelId);
}
