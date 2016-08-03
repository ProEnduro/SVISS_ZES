/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.User;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class TimeSynchronisationTask implements Runnable {
    
    User_DAO user_DAO;
    
    public TimeSynchronisationTask() {
        user_DAO = DAOFactory.getDAOFactory().getUserDAO();
    }
    
    @Override
    public void run() {
        System.out.println("More Holiday");
        Date today = new Date();
        List<User> users = user_DAO.getUserByDisabled(Boolean.FALSE);
        for (User u : users) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("MMdd");
                String t = fmt.format(today);
                String h = fmt.format(u.getHiredate());
                if (t.equals(h)) {
                    u.setVacationLeft(u.getVacationLeft() + 25);
                    user_DAO.update(u);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Even more Holiday");
    }
    
}
