/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Martin Six
 */
@FacesConverter(value = "localDateTimeConverter")
public class LocalDateTimeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().validationFailed();
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        LocalDateTime ldt = (LocalDateTime) value;
        return ldt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy "));

    }

}
