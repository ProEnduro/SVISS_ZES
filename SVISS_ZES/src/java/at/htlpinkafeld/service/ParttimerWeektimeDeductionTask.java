/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.beans.ScheduledTaskManagementBean;
import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.context.RequestContext;

/**
 *
 * @author SVISS_NES
 */
public class ParttimerWeektimeDeductionTask implements Runnable {

    private static final User_DAO USER_DAO;
    private static final SollZeiten_DAO SOLLZEITEN_DAO;
    

    static {
        USER_DAO = DAOFactory.getDAOFactory().getUserDAO();
        SOLLZEITEN_DAO = DAOFactory.getDAOFactory().getSollZeitenDAO();
    }

    public ParttimerWeektimeDeductionTask() {
        
//        for(User u: USER_DAO.getUserByParttimer(Boolean.TRUE)){
//            //Logger.getLogger(ParttimerWeektimeDeductionTask.class.getName()).log(Level.INFO, "Parttimer: " + u.getUsername());
//            //this.rc.execute("Primefaces.info('Parttimer: "+ u.getUsername() +"');");
//            SollZeit sz = SOLLZEITEN_DAO.getSollZeitenByUser_Current(u);
//            
//            Logger.getLogger(ParttimerWeektimeDeductionTask.class.getName()).log(Level.INFO, "Parttimer: " + u.getUsername() + " " + sz);
//            if (sz.getSollStartTimeMap().isEmpty() && sz.getSollEndTimeMap().isEmpty()) {
//                //this.rc.execute("Primefaces.info('No Solltime: "+ u.getUsername() +"');");
//                //this.rc.execute("Primefaces.info('"+u.getWeekTime().intValue()+"');");
//                Logger.getLogger(ParttimerWeektimeDeductionTask.class.getName()).log(Level.INFO, "No Solltime: " + u.getUsername() + " Weektime: " + u.getWeekTime().intValue());
//            }
//            
//        }
        
    }

    @Override
    public void run() {
        List<User> ulist = USER_DAO.getUserByParttimer(Boolean.TRUE);
        List<User> parttimersWithoutSollzeit = new ArrayList<>();

        for (User u : ulist) {
            SollZeit sz = SOLLZEITEN_DAO.getSollZeitenByUser_Current(u);
            if (sz.getSollStartTimeMap().isEmpty() && sz.getSollEndTimeMap().isEmpty()) {
                parttimersWithoutSollzeit.add(u);
            }
        }

        for (User u : parttimersWithoutSollzeit) {
            u.setOverTimeLeft(u.getOverTimeLeft() - u.getWeekTime().intValue() * 60);
            USER_DAO.update(u);
        }

    }

}
