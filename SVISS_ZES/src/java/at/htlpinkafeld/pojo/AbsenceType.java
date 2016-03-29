/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

/**
 *
 * @author Martin Six
 */
public class AbsenceType {

    private int absenceID;
    private String absenceName;

    public AbsenceType(int absenceID, String absenceName) {
        this.absenceID = absenceID;
        this.absenceName = absenceName;
    }

    public int getAbsenceID() {
        return absenceID;
    }

    public void setAbsenceID(int absenceID) {
        this.absenceID = absenceID;
    }

    public String getAbsenceName() {
        return absenceName;
    }

    public void setAbsenceName(String absenceName) {
        this.absenceName = absenceName;
    }

    @Override
    public String toString() {
        return "AbsenceType{" + "absenceID=" + absenceID + ", absenceName=" + absenceName + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + this.absenceID;
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
        if (this.absenceID != other.absenceID) {
            return false;
        }
        return true;
    }

}
