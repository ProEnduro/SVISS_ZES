/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import java.io.Serializable;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Martin Six
 */
@XmlRootElement
public class AbsenceType implements Serializable {

    private static final long serialVersionUID = 8189138937691793171L;

    private Integer absenceTypeID;
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
    public AbsenceType(Integer absenceID, String absenceName) {
        this.absenceTypeID = absenceID;
        this.absenceName = absenceName;
    }

    public Integer getAbsenceTypeID() {
        return absenceTypeID;
    }

    public void setAbsenceTypeID(Integer absenceTypeID) {
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
        if (!Objects.equals(this.absenceName, other.absenceName)) {
            return false;
        }
        if (!Objects.equals(this.absenceTypeID, other.absenceTypeID)) {
            return false;
        }
        return true;
    }

    

}
