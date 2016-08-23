/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import java.io.Serializable;

/**
 *
 * @author Martin Six
 */
public class AbsenceType implements Serializable {

    private int absenceTypeID;
    private String absenceName;

    public AbsenceType() {
        absenceTypeID = -1;
    }

    public AbsenceType(AbsenceType at) {
        this.absenceTypeID = at.absenceTypeID;
        this.absenceName = at.absenceName;
    }

    public AbsenceType(String absenceName) {
        this();
        this.absenceName = absenceName;
    }

    @Deprecated
    public AbsenceType(int absenceID, String absenceName) {
        this.absenceTypeID = absenceID;
        this.absenceName = absenceName;
    }

    public int getAbsenceTypeID() {
        return absenceTypeID;
    }

    public void setAbsenceTypeID(int absenceTypeID) {
        this.absenceTypeID = absenceTypeID;
    }

    public String getAbsenceName() {
        return absenceName;
    }

    public void setAbsenceName(String absenceName) {
        this.absenceName = absenceName;
    }

    @Override
    public String toString() {
        return "AbsenceType{" + "absenceTypeID=" + absenceTypeID + ", absenceName=" + absenceName + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + this.absenceTypeID;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbsenceType other = (AbsenceType) obj;
        if (this.absenceTypeID != other.absenceTypeID) {
            return false;
        }
        return true;
    }

}
