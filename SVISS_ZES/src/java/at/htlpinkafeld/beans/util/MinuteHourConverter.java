/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * A converter which converts minutes to hours for visual display
 *
 * @author Martin Six
 */
@FacesConverter(value = "minuteHourConverter")
public class MinuteHourConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        return Double.parseDouble(string) * 60;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return String.valueOf(Double.valueOf(o.toString()) / 60.0);
    }

}
