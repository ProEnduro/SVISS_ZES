/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.UserHistory_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserHistoryEntry;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class UpdateUserHistoryTask implements Runnable {

    private final User_DAO user_DAO;
    private final UserHistory_DAO history_DAO;

    public UpdateUserHistoryTask() {
        user_DAO = DAOFactory.getDAOFactory().getUserDAO();
        history_DAO = DAOFactory.getDAOFactory().getUserHistoryDAO();
    }

    @Override
    public void run() {
        LocalDate today = LocalDate.now();
        if (today.getDayOfMonth() == 1) {
            List<User> users = user_DAO.getUserByDisabled(Boolean.FALSE);
            users.forEach((u) -> {
                history_DAO.insert(new UserHistoryEntry(today.atStartOfDay().minusHours(1).minusMinutes(1), u, u.getOverTimeLeft(), u.getVacationLeft()));
            });
        }
    }

}
