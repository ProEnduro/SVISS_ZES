/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.Absence_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.AbsenceTypeNew;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.service.TimeConverterService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class Absence_InMemoryDAO extends Base_InMemoryDAO<Absence> implements Absence_DAO {

    protected Absence_InMemoryDAO() {
        super(new ArrayList<>());
        User_DAO uDAO = new User_InMemoryDAO();
        super.insert(new Absence(0, uDAO.getUser(1), AbsenceTypeNew.MEDICAL_LEAVE, LocalDateTime.of(2016, 4, 4, 0, 0, 0), LocalDateTime.of(2016, 4, 6, 0, 0, 0), "Cold", true));
        super.insert(new Absence(uDAO.getUser(2), AbsenceTypeNew.TIME_COMPENSATION, LocalDateTime.of(2016, 4, 4, 0, 0, 0), LocalDateTime.of(2016, 4, 6, 0, 0, 0)));

        super.insert(new Absence(0, uDAO.getUser(9), AbsenceTypeNew.BUSINESSRELATED_ABSENCE, LocalDateTime.of(2016, 7, 8, 8, 0, 0), LocalDateTime.of(2016, 7, 8, 13, 30, 0), "I took an Arrow in the knee!", true));
        super.insert(new Absence(uDAO.getUser(5), AbsenceTypeNew.MEDICAL_LEAVE, LocalDateTime.of(2016, 9, 30, 0, 0, 0), LocalDateTime.of(2016, 9, 30, 0, 0, 0)));
        super.insert(new Absence(uDAO.getUser(7), AbsenceTypeNew.HOLIDAY, LocalDateTime.of(2016, 2, 12, 0, 0, 0), LocalDateTime.of(2016, 2, 12, 0, 0, 0)));
        super.insert(new Absence(uDAO.getUser(8), AbsenceTypeNew.BUSINESSRELATED_ABSENCE, LocalDateTime.of(2016, 12, 25, 10, 30, 0), LocalDateTime.of(2016, 12, 25, 16, 22, 0)));
        super.insert(new Absence(uDAO.getUser(2), AbsenceTypeNew.TIME_COMPENSATION, LocalDateTime.of(2016, 8, 17, 0, 0, 0), LocalDateTime.of(2016, 8, 18, 0, 0, 0)));
        super.insert(new Absence(uDAO.getUser(6), AbsenceTypeNew.TIME_COMPENSATION, LocalDateTime.of(2016, 8, 26, 12, 30, 0), LocalDateTime.of(2016, 8, 27, 0, 0, 0)));

        super.insert(new Absence(uDAO.getUser(12), AbsenceTypeNew.MEDICAL_LEAVE, LocalDateTime.of(2016, 8, 2, 9, 0, 0), LocalDateTime.of(2016, 8, 3, 0, 0, 0)));
        super.insert(new Absence(0, uDAO.getUser(9), AbsenceTypeNew.BUSINESSRELATED_ABSENCE, LocalDateTime.of(2016, 8, 18, 8, 0, 0), LocalDateTime.of(2016, 8, 18, 12, 30, 0), "PC exploded - BOOM!", true));
        super.insert(new Absence(0, uDAO.getUser(7), AbsenceTypeNew.BUSINESSRELATED_ABSENCE, LocalDateTime.of(2016, 8, 22, 8, 0, 0), LocalDateTime.of(2016, 8, 22, 13, 30, 0), "OH my GOD a TRIPPLE!", true));
        super.insert(new Absence(0, uDAO.getUser(4), AbsenceTypeNew.TIME_COMPENSATION, LocalDateTime.of(2016, 7, 8, 8, 0, 0), LocalDateTime.of(2016, 7, 8, 16, 30, 0), "ITS MA TIME!", true));
        super.insert(new Absence(0, uDAO.getUser(2), AbsenceTypeNew.BUSINESSRELATED_ABSENCE, LocalDateTime.of(2016, 7, 15, 8, 0, 0), LocalDateTime.of(2016, 7, 15, 17, 30, 0), "guess what?!", true));
        super.insert(new Absence(0, uDAO.getUser(6), AbsenceTypeNew.BUSINESSRELATED_ABSENCE, LocalDateTime.of(2016, 6, 26, 8, 0, 0), LocalDateTime.of(2016, 6, 26, 11, 30, 0), "ICECREAM, I WANT ICECREAM!", true));

        super.insert(new Absence(0, uDAO.getUser(5), AbsenceTypeNew.MEDICAL_LEAVE, LocalDateTime.of(2016, 8, 26, 10, 0, 0), LocalDateTime.of(2016, 8, 27, 0, 0, 0), "Reason: Kevin", true));
        super.insert(new Absence(uDAO.getUser(7), AbsenceTypeNew.HOLIDAY, LocalDateTime.of(2016, 7, 18, 0, 0, 0), LocalDateTime.of(2016, 7, 19, 0, 0, 0)));
        super.insert(new Absence(uDAO.getUser(14), AbsenceTypeNew.HOLIDAY, LocalDateTime.of(2016, 8, 14, 8, 30, 0), LocalDateTime.of(2016, 8, 15, 0, 0, 0)));

    }

    @Override
    public List<Absence> getAbsencesByUser(User u) {
        List<Absence> aList = new LinkedList<>();
        super.getList().stream().filter((a) -> (a.getUser().equals(u))).forEachOrdered((a) -> {
            aList.add(clone(a));
        });
        return aList;
    }

    @Override
    public List<Absence> getAbsencesByAbsenceType(AbsenceTypeNew at) {
        List<Absence> aList = new LinkedList<>();
        super.getList().stream().filter((a) -> (a.getAbsenceType().equals(at))).forEachOrdered((a) -> {
            aList.add(clone(a));
        });
        return aList;
    }

    @Override
    public List<Absence> getAbsencesByAbsenceType_User(AbsenceTypeNew at, User u) {
        List<Absence> aList = new LinkedList<>();
        super.getList().stream().filter((a) -> (a.getAbsenceType().equals(at) && a.getUser().equals(u))).forEachOrdered((a) -> {
            aList.add(clone(a));
        });
        return aList;
    }

    @Override
    public List<Absence> getAbsencesByAcknowledgment(boolean acknowledged) {
        List<Absence> aList = new LinkedList<>();
        super.getList().stream().filter((a) -> (a.isAcknowledged() == acknowledged)).forEachOrdered((a) -> {
            aList.add(clone(a));
        });
        return aList;
    }

    @Override
    public List<Absence> getAbsencesByUser_Acknowledgment(User u, boolean acknowledged) {
        List<Absence> aList = new LinkedList<>();
        super.getList().stream().filter((a) -> (a.isAcknowledged() == acknowledged && a.getUser().equals(u))).forEachOrdered((a) -> {
            aList.add(clone(a));
        });
        return aList;
    }

    @Override
    public List<Absence> getAbsencesBetweenDates(java.util.Date startDate, java.util.Date endDate) {
        List<Absence> aList = new LinkedList<>();
        super.getList().stream().filter((a) -> (((a.getStartTime().isAfter(TimeConverterService.convertDateToLocalDateTime(startDate)) || a.getStartTime().isEqual(TimeConverterService.convertDateToLocalDateTime(startDate)))
                && a.getStartTime().isBefore(TimeConverterService.convertDateToLocalDateTime(endDate)))
                || ((a.getEndTime().isAfter(TimeConverterService.convertDateToLocalDateTime(startDate)) || a.getEndTime().isEqual(TimeConverterService.convertDateToLocalDateTime(startDate)))
                        && a.getEndTime().isBefore(TimeConverterService.convertDateToLocalDateTime(endDate))))).forEachOrdered((a) -> {
                            aList.add(clone(a));
        });
        return aList;
    }

    @Override
    public List<Absence> getAbsencesByUser_BetweenDates(User user, java.util.Date startDate, java.util.Date endDate) {
        List<Absence> aList = new LinkedList<>();
        super.getList().stream().filter((a) -> (a.getUser().equals(user))).filter((a) -> (((a.getStartTime().isAfter(TimeConverterService.convertDateToLocalDateTime(startDate)) || a.getStartTime().isEqual(TimeConverterService.convertDateToLocalDateTime(startDate)))
                && a.getStartTime().isBefore(TimeConverterService.convertDateToLocalDateTime(endDate)))
                || ((a.getEndTime().isAfter(TimeConverterService.convertDateToLocalDateTime(startDate)) || a.getEndTime().isEqual(TimeConverterService.convertDateToLocalDateTime(startDate)))
                        && a.getEndTime().isBefore(TimeConverterService.convertDateToLocalDateTime(endDate))))).forEachOrdered((a) -> {
                            aList.add(clone(a));
        });
        return aList;
    }

    @Override
    public List<Absence> getAbsencesByApprover_Acknowledgment_BetweenDates(User approver, boolean acknowledged, java.util.Date startDate, java.util.Date endDate) {
        List<Absence> aList = new LinkedList<>();
        super.getList().stream().filter((a) -> (a.isAcknowledged() == acknowledged)).filter((a) -> (a.getUser().getApprover().contains(approver))).filter((a) -> (((a.getStartTime().isAfter(TimeConverterService.convertDateToLocalDateTime(startDate)) || a.getStartTime().isEqual(TimeConverterService.convertDateToLocalDateTime(startDate)))
                && a.getStartTime().isBefore(TimeConverterService.convertDateToLocalDateTime(endDate)))
                || ((a.getEndTime().isAfter(TimeConverterService.convertDateToLocalDateTime(startDate)) || a.getEndTime().isEqual(TimeConverterService.convertDateToLocalDateTime(startDate)))
                        && a.getEndTime().isBefore(TimeConverterService.convertDateToLocalDateTime(endDate))))).forEachOrdered((a) -> {
                            aList.add(clone(a));
        });
        return aList;
    }

    @Override
    public List<Absence> getAbsencesByAcknowledgment_BetweenDates(boolean acknowledged, java.util.Date startDate, java.util.Date endDate) {
        List<Absence> aList = new LinkedList<>();
        super.getList().stream().filter((a) -> (a.isAcknowledged() == acknowledged)).filter((a) -> (((a.getStartTime().isAfter(TimeConverterService.convertDateToLocalDateTime(startDate)) || a.getStartTime().isEqual(TimeConverterService.convertDateToLocalDateTime(startDate)))
                && a.getStartTime().isBefore(TimeConverterService.convertDateToLocalDateTime(endDate)))
                || ((a.getEndTime().isAfter(TimeConverterService.convertDateToLocalDateTime(startDate)) || a.getEndTime().isEqual(TimeConverterService.convertDateToLocalDateTime(startDate)))
                        && a.getEndTime().isBefore(TimeConverterService.convertDateToLocalDateTime(endDate))))).forEachOrdered((a) -> {
                            aList.add(clone(a));
        });
        return aList;
    }

    @Override
    protected Absence clone(Absence entity) {
        return new Absence(entity);
    }

    @Override
    protected void setID(Absence entity, int id) {
        entity.setAbsenceID(id);
    }

    @Override
    public void deleteAllAbsenceFromUser(User o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
