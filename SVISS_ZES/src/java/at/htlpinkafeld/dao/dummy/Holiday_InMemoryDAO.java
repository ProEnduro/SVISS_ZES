/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.Holiday_DAO;
import at.htlpinkafeld.pojo.Holiday;
import at.htlpinkafeld.service.TimeConverterService;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class Holiday_InMemoryDAO extends Base_InMemoryDAO<Holiday> implements Holiday_DAO {

    protected Holiday_InMemoryDAO() {
        super(new ArrayList<>());
    }

    @Override
    protected Holiday clone(Holiday entity) {
        return new Holiday(entity);
    }

    @Override
    protected void setID(Holiday entity, int id) {

    }

    @Override
    public List<Holiday> getHolidayBetweenDates(Date startDate, Date endDate) {
        List<Holiday> holidays = new ArrayList<>();
        for (Holiday h : getList()) {
            if ((h.getHolidayDate().isAfter(TimeConverterService.convertDateToLocalDateTime(startDate).toLocalDate()) || h.getHolidayDate().isEqual(TimeConverterService.convertDateToLocalDateTime(startDate).toLocalDate()))
                    && h.getHolidayDate().isBefore(TimeConverterService.convertDateToLocalDateTime(endDate).toLocalDate())) {
                holidays.add(clone(h));
            }
        }
        return holidays;
    }

}
