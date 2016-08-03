/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Martin Six
 */
@FacesConverter(value = "localDateConverter")
public class LocalDateConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        return LocalDate.parse(string, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        LocalDate ld = (LocalDate) o;
        return ld.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

}
