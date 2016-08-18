/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.Absence;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class AbsenceCleaningTask implements Runnable {

    private static final User_DAO USER_DAO;
    private static final Absence_DAO ABSENCE_DAO;

    static {
        USER_DAO = DAOFactory.getDAOFactory().getUserDAO();
        ABSENCE_DAO = DAOFactory.getDAOFactory().getAbsenceDAO();
    }

    @Override
    public void run() {
        LocalDateTime ldtstart = LocalDate.now().minusWeeks(1).atStartOfDay();
        LocalDateTime ldtend = ldtstart.withHour(23).withMinute(59).withSecond(59);
        List<Absence> absences = ABSENCE_DAO.getAbsencesByAcknowledgment_BetweenDates(false, TimeConverterService.convertLocalDateTimeToDate(ldtstart), TimeConverterService.convertLocalDateTimeToDate(ldtend));
        for (Absence a : absences) //Email verschicken
        {
            EmailService.sendReminderAcknowledgementEmail(a, USER_DAO.getApprover(a.getUser()));
        }

        ldtstart = ldtstart.minusWeeks(1);
        ldtend = ldtend.minusWeeks(1);
        absences = ABSENCE_DAO.getAbsencesByAcknowledgment_BetweenDates(false, TimeConverterService.convertLocalDateTimeToDate(ldtstart), TimeConverterService.convertLocalDateTimeToDate(ldtend));
        for (Absence a : absences) //Email verschicken
        {
            EmailService.sendAbsenceDeletedEmailBySystem(a, USER_DAO.getApprover(a.getUser()));
            ABSENCE_DAO.delete(a);
        }
    }

}
