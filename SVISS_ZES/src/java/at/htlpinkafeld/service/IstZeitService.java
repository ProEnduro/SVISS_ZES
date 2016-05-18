/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.interf.DAOFactory;
import at.htlpinkafeld.dao.interf.WorkTime_DAO;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ÐarkHell2
 */
public class IstZeitService {

    static WorkTime_DAO workDAO;
    static WorkTime workT;
    static WorkTime test;

    public IstZeitService() {
    }

    public static void addIstTime(User user, Date startTime, Date endTime) {
        workT = new WorkTime(user, startTime, endTime, 0, "", "");
        workDAO = DAOFactory.getDAOFactory().getWorkTimeDAO();
        workDAO.insert(workT);
        List myList = workDAO.getList();

        test = (WorkTime) myList.get(2);
        System.out.println("start: " + test.getStartTime());
        System.out.println("ende: " + test.getEndTime());

    }
}
