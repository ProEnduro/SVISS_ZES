/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.interf;

import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.AbsenceType;
import at.htlpinkafeld.pojo.User;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public interface Absence_DAO extends Base_DAO<Absence> {

    public abstract List<Absence> getAbsencesByUser(User u);

    public abstract List<Absence> getAbsencesByAbsenceType(AbsenceType at);

    public abstract List<Absence> getAbsencesByAbsenceTypeAndUser(AbsenceType at, User u);

    public abstract List<Absence> getAbsencesByAcknowledgment(boolean acknowledged);

    public abstract List<Absence> getAbsencesByUserAndAcknowledgment(User u, boolean acknowledged);

    public abstract List<Absence> getAbsencesBetweenDates(Date startDateU, Date endDateU);

    public abstract List<Absence> getAbsencesByUserBetweenDates(User user, Date startDateU, Date endDateU);

}
