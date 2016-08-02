/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans.util;

import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.service.AccessRightsService;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Martin Six
 */
@FacesConverter(value = "accesslevelconv", forClass = AccessLevel.class)
public class AccessLevelConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        return AccessRightsService.getAccessLevelFromName(string);
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        return ((AccessLevel) o).getAccessLevelName();
    }

}
