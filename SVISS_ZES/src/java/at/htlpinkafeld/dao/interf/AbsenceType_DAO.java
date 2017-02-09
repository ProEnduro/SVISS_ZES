/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.interf;

import at.htlpinkafeld.pojo.AbsenceType;

/**
 *
 * @author Martin Six
 */
public interface AbsenceType_DAO extends Base_DAO<AbsenceType> {

    public static final String MEDICAL_LEAVE = "Krankenstand";
    public static final String BUSINESSRELATED_ABSENCE = "unternehmensbedingte Abwesenheit";
    public static final String TIMECOMPENSATION = "Zeitausgleich";
    public static final String HOLIDAY = "Urlaub";

    public abstract AbsenceType getAbsenceTypeByID(int absenceTypeID);
}
