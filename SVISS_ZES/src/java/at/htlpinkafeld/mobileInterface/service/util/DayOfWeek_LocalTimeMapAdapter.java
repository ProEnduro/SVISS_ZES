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
 * {@link XmlAdapter} for {@link DayOfWeek}
 *
 * @author Martin Six
 */
public class DayOfWeek_LocalTimeMapAdapter extends XmlAdapter<String, Map<DayOfWeek, LocalTime>> {

    @Override
    public Map<DayOfWeek, LocalTime> unmarshal(String mapString) throws Exception {
        Map<DayOfWeek, LocalTime> newMap = new HashMap<>();
        for (String entryS : mapString.split(";")) {
            String[] strParts = entryS.split(",");
            newMap.put(DayOfWeek.valueOf(strParts[0]), LocalTime.parse(strParts[1]));
        }
        return newMap;
    }

    @Override 
    public String marshal(Map<DayOfWeek, LocalTime> map) throws Exception {
        String s = "";
        for (Map.Entry<DayOfWeek, LocalTime> entry : map.entrySet()) {
            s += entry.getKey().name() + "," + entry.getValue().format(DateTimeFormatter.ISO_LOCAL_TIME) + ";";
        }
        return s;
    }

}
