/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author ÐarkHell2
 */
@FacesConverter(value = "acknowledgeconverter")
public class AcknowledgeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        boolean retVal = false;

        if (string != null && string.equals("true")) {
            try {
                retVal = true;
            } catch (Exception e) {
                FacesMessage fm = new FacesMessage("Error in AcknowledgeConverter!");
                throw new ConverterException(fm);
            }
        }
        return retVal;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        String retVal = "Nein";

        if (o != null && (boolean) o == true) {
            retVal = "Ja";
            return retVal;
        }
        return retVal;
    }

}
