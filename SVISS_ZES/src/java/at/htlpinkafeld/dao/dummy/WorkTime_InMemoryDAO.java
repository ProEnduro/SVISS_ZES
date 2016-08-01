/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.dao.interf.WorkTime_DAO;
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
public class WorkTime_InMemoryDAO extends Base_InMemoryDAO<WorkTime> implements WorkTime_DAO {

    protected WorkTime_InMemoryDAO() {
        super(new ArrayList<>());
        User_DAO uDAO = new User_InMemoryDAO();
        super.insert(new WorkTime(uDAO.getUser(1), new GregorianCalendar(2016, 3, 14, 8, 0).getTime(), new GregorianCalendar(2016, 3, 14, 16, 30).getTime(), 30, "Start Test", "End Test"));
        super.insert(new WorkTime(uDAO.getUser(2), new GregorianCalendar(2016, 3, 14, 9, 0).getTime(), new GregorianCalendar(2016, 3, 14, 16, 0).getTime(), 30, "Start Later Test", "End Early Test"));
    }

    @Override
    public List<WorkTime> getWorkTimesByUser(User u) {
        List<WorkTime> wtList = new LinkedList<>();
        for (WorkTime wt : super.getList()) {
            if (wt.getUser().equals(u)) {
                wtList.add(clone(wt));
            }
        }
        return wtList;
    }

    @Override
    protected WorkTime clone(WorkTime entity) {
        return new WorkTime(entity);
    }

    @Override
    protected void setID(WorkTime entity, int id) {
        entity.setTimeID(id);
    }

}
