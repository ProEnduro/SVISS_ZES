/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service.util;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * {@link XmlAdapter} for a Map of {@link DayOfWeek} and {@link LocalTime}
 *
 * @author Martin Six
 */
public class LocalTimeMapAdapter extends XmlAdapter<String, Map<DayOfWeek, LocalTime>> {

    @Override
    public Map<DayOfWeek, LocalTime> unmarshal(String marshalledString) throws Exception {
        Map<DayOfWeek, LocalTime> unmarshaledMap = new HashMap<>();
        for (String entryS : marshalledString.split(";")) {
            String[] stringParts = entryS.split(":");
            unmarshaledMap.put(DayOfWeek.valueOf(stringParts[0]), LocalTime.parse(stringParts[1], DateTimeFormatter.ISO_LOCAL_TIME));
        }

        return unmarshaledMap;
    }

    @Override
    public String marshal(Map<DayOfWeek, LocalTime> localTimes) throws Exception {
        String marshalledString = "";
        marshalledString = localTimes.entrySet().stream().map((localTime) -> localTime.getKey().name() + ":" + DateTimeFormatter.ISO_LOCAL_TIME.format(localTime.getValue()) + ";").reduce(marshalledString, String::concat);

        return marshalledString;
    }

}
