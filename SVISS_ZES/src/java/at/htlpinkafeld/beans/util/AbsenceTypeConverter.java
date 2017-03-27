/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util;

import at.htlpinkafeld.pojo.AbsenceTypeNew;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * A Converter for the AbsenceType-Enum
 *
 * @author Martin Six
 */
@FacesConverter(value = "absenceTypeConv", forClass = AbsenceTypeNew.class)
public class AbsenceTypeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        return AbsenceTypeNew.valueOf(string);
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return ((AbsenceTypeNew) o).name();
    }

}
