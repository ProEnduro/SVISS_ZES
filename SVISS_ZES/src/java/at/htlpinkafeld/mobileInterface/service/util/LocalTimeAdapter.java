/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * {@link XmlAdapter} for {@link LocalTime}
 *
 * @author Martin Six
 */
public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {

    @Override
    public LocalTime unmarshal(String dateString) throws Exception {
        return LocalTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    @Override
    public String marshal(LocalTime localTime) throws Exception {
        return DateTimeFormatter.ISO_LOCAL_TIME.format(localTime);
    }

}
