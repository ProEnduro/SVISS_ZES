/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util;

import at.htlpinkafeld.pojo.AbsenceType;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author Martin Six
 */
public class AbsenceTypeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        string = string.substring(string.indexOf('{'), string.length());
        String[] parts = string.split(",");
        return new AbsenceType(Integer.parseInt(parts[0].trim()), parts[1].trim());
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return o.toString();
    }

}
