/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.User_DAO;
import at.htlpinkafeld.dao.WorkTime_DAO;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class WorkTime_DummyDAO extends Base_DummyDAO<WorkTime> implements WorkTime_DAO {

    public WorkTime_DummyDAO() {
        super(new ArrayList<>());
        User_DAO uDAO = new User_DummyDAO();
        super.insert(new WorkTime(1, uDAO.getUser(1), new GregorianCalendar(2016, 3, 14, 8, 0).getTime(), new GregorianCalendar(2016, 3, 14, 16, 30).getTime(), 30, "Start Test", "End Test"));
        super.insert(new WorkTime(2, uDAO.getUser(2), new GregorianCalendar(2016, 3, 14, 9, 0).getTime(), new GregorianCalendar(2016, 3, 14, 16, 0).getTime(), 30, "Start Later Test", "End Early Test"));
    }

    @Override
    public List<WorkTime> getWorkTimesByUser(User u) {
        List<WorkTime> wtList = new LinkedList<>();
        for (WorkTime wt : super.getList()) {
            if (wt.getUser().equals(u)) {
                wtList.add(wt);
            }
        }
        return wtList;
    }

}
