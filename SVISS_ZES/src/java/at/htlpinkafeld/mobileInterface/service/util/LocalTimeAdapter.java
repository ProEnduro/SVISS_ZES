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
 * @author masix
 */
public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {

    @Override
    public LocalTime unmarshal(String timeString) throws Exception {
        return LocalTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    @Override
    public String marshal(LocalTime time) throws Exception {
        return time.format(DateTimeFormatter.ISO_LOCAL_TIME);
    }

}
