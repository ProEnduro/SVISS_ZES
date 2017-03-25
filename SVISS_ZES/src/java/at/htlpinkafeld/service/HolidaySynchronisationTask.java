/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.dao.util.DAOException;
import at.htlpinkafeld.pojo.User;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Six
 */
public class HolidaySynchronisationTask implements Runnable {

    User_DAO user_DAO;

    public HolidaySynchronisationTask() {
        user_DAO = DAOFactory.getDAOFactory().getUserDAO();
    }

    @Override
    public void run() {
        Date today = new Date();
        List<User> users = user_DAO.getUserByDisabled(Boolean.FALSE);
        users.forEach((u) -> {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("MMdd");
                String t = fmt.format(today);
                String h = u.getHiredate().format(DateTimeFormatter.ofPattern("MMdd"));
                if (t.equals(h)) {
                    u.setVacationLeft(u.getVacationLeft() + 25);
                    user_DAO.update(u);
                }
            } catch (DAOException e) {
                Logger.getLogger(HolidaySynchronisationTask.class.getName()).log(Level.SEVERE, null, e);

            }
        });
    }

}
