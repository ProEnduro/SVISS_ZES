/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.SollZeiten_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.dao.interf.WorkTime_DAO;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import at.htlpinkafeld.service.TimeConverterService;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        super.insert(new WorkTime(uDAO.getUser(1), LocalDateTime.of(2016, 3, 14, 8, 0), LocalDateTime.of(2016, 3, 14, 16, 30), 30, "Start Test", "End Test"));
        super.insert(new WorkTime(uDAO.getUser(2), LocalDateTime.of(2016, 3, 14, 9, 0), LocalDateTime.of(2016, 3, 14, 16, 0), 30, "Start Later Test", "End Early Test"));

        super.insert(new WorkTime(uDAO.getUser(3), LocalDateTime.of(2016, 3, 19, 8, 39), LocalDateTime.of(2016, 3, 19, 9, 17), 30, "Start changing CPU", "End changing CPU"));
        super.insert(new WorkTime(uDAO.getUser(5), LocalDateTime.of(2016, 6, 7, 11, 22), LocalDateTime.of(2016, 6, 7, 13, 16), 30, "Start fixing RAM", "End fixing RAM"));
        super.insert(new WorkTime(uDAO.getUser(4), LocalDateTime.of(2016, 4, 22, 15, 50), LocalDateTime.of(2016, 4, 22, 16, 22), 30, "Start complaining about something", "End complaining about something"));
        super.insert(new WorkTime(uDAO.getUser(6), LocalDateTime.of(2016, 7, 16, 9, 33), LocalDateTime.of(2016, 7, 16, 13, 30), 30, "Start kicking the dumb PC", "End kicking the dumb PC"));
        super.insert(new WorkTime(uDAO.getUser(8), LocalDateTime.of(2016, 5, 28, 12, 0), LocalDateTime.of(2016, 5, 28, 16, 55), 30, "Start executing Trojan.exe", "End executing Trojan.exe"));

        super.insert(new WorkTime(uDAO.getUser(7), LocalDateTime.of(2016, 9, 14, 16, 0), LocalDateTime.of(2016, 9, 14, 16, 59), 30, "Start buying GPUs", "End buying GPUs"));
        super.insert(new WorkTime(uDAO.getUser(10), LocalDateTime.of(2016, 11, 10, 8, 30), LocalDateTime.of(2016, 11, 10, 11, 49), 30, "Start doing nothing", "End doing nothing"));
        super.insert(new WorkTime(uDAO.getUser(9), LocalDateTime.of(2016, 10, 1, 11, 9), LocalDateTime.of(2016, 10, 1, 17, 55), 30, "Start playing DiabloIII", "End playing DiabloIII"));

        super.insert(new WorkTime(uDAO.getUser(11), LocalDateTime.of(2016, 8, 28, 8, 0), LocalDateTime.of(2016, 8, 28, 16, 59), 30, "Shopping office tools", "Payd 3248.99€"));
        super.insert(new WorkTime(uDAO.getUser(12), LocalDateTime.of(2016, 1, 18, 8, 0), LocalDateTime.of(2016, 1, 18, 17, 00), 30, "Holding the door!", "Stop holding the door!"));
        super.insert(new WorkTime(uDAO.getUser(14), LocalDateTime.of(2016, 6, 19, 10, 0), LocalDateTime.of(2016, 6, 19, 14, 23), 30, "Kicking the server!", "Server has a nice shape now!"));

        SollZeiten_DAO szdao = new SollZeiten_InMemoryDAO();
        super.getList().forEach((wt) -> {
            SollZeit sz = szdao.getSollZeitenByUser_Current(wt.getUser());
            if (sz != null) {
                DayOfWeek dow = wt.getStartTime().getDayOfWeek();
                wt.setSollStartTime(sz.getSollStartTime(dow));
                wt.setSollEndTime(sz.getSollEndTime(dow));
            }
        });
    }

    @Override
    public List<WorkTime> getWorkTimesByUser(User u) {
        List<WorkTime> wtList = new LinkedList<>();
        super.getList().stream().filter((wt) -> (wt.getUser().equals(u))).forEachOrdered((wt) -> {
            wtList.add(clone(wt));
        });
        return wtList;
    }

    @Override
    public List<WorkTime> getWorkTimesBetweenDates(java.util.Date startDate, java.util.Date endDate) {
        List<WorkTime> wtList = new LinkedList<>();
        super.getList().stream().filter((wt) -> ((wt.getStartTime().isAfter(TimeConverterService.convertDateToLocalDateTime(startDate)) || wt.getStartTime().isEqual(TimeConverterService.convertDateToLocalDateTime(startDate)))
                && wt.getEndTime().isBefore(TimeConverterService.convertDateToLocalDateTime(endDate)))).forEachOrdered((wt) -> {
                    wtList.add(clone(wt));
        });
        return wtList;
    }

    @Override
    public List<WorkTime> getWorkTimesFromUserBetweenDates(User user, java.util.Date startDate, java.util.Date endDate) {
        List<WorkTime> wtList = new LinkedList<>();
        super.getList().stream().filter((wt) -> (wt.getUser().equals(user))).filter((wt) -> ((wt.getStartTime().isAfter(TimeConverterService.convertDateToLocalDateTime(startDate)) || wt.getStartTime().isEqual(TimeConverterService.convertDateToLocalDateTime(startDate)))
                && wt.getEndTime().isBefore(TimeConverterService.convertDateToLocalDateTime(endDate)))).forEachOrdered((wt) -> {
                    wtList.add(clone(wt));
        });
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
