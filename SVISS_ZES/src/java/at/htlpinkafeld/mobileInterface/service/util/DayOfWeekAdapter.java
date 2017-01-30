/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service.util;

import java.time.DayOfWeek;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Martin Six
 */
public class DayOfWeekAdapter extends XmlAdapter<String, DayOfWeek> {

    @Override
    public DayOfWeek unmarshal(String dowString) throws Exception {
        return DayOfWeek.valueOf(dowString);
    }

    @Override
    public String marshal(DayOfWeek dow) throws Exception {
        return dow.name();
    }

}
