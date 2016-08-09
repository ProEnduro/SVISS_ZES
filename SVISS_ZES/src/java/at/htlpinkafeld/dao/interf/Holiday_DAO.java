/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.interf;

import at.htlpinkafeld.pojo.Holiday;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public interface Holiday_DAO extends Base_DAO<Holiday> {

    public abstract List<Holiday> getHolidayBetweenDates(Date startDate, Date endDate);
}
