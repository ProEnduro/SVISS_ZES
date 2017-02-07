/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * {@link XmlAdapter} for {@link LocalDate}
 *
 * @author Martin Six
 */
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public LocalDate unmarshal(String dateString) throws Exception {
        return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
    }

    @Override
    public String marshal(LocalDate localDate) throws Exception {
        return DateTimeFormatter.ISO_DATE.format(localDate);
    }
}
