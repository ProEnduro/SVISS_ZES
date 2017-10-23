/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util.Converter;

import at.htlpinkafeld.pojo.UserProxy;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * A Converter for Users
 *
 * @author Martin Six
 */
@FacesConverter(value = "userConverter")
public class UserConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string != null && !string.contentEquals("")) {
            return UserProxy.fromString(string);
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o != null) {
            return o.toString();
        } else {
            return null;
        }
    }

}
