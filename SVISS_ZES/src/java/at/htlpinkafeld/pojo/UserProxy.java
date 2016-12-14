/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.mobileInterface.service.util.LocalDateAdapter;
import at.htlpinkafeld.service.AccessRightsService;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Martin Six
 */
@XmlRootElement
public class UserProxy implements User {

    private static final User_DAO USER_DAO;
    private User user;

    static {
        USER_DAO = DAOFactory.getDAOFactory().getUserDAO();
    }

    public UserProxy() {
        user = new UserImpl();
    }

    public UserProxy(User u) {
        user = new UserImpl(u);
        if (u.ApproverInitialized()) {
            user.setApprover(u.getApprover());
        }
    }

    @Deprecated
    public UserProxy(Integer userNr, AccessLevel accessLevel, String PersName, Integer vacationLeft, Integer overTimeLeft, String username, String email, LocalDate hiredate, String pass, Double weekTime, boolean disabled) {
        user = new UserImpl(userNr, accessLevel, PersName, vacationLeft, overTimeLeft, username, email, hiredate, pass, weekTime, disabled);
    }

    public UserProxy(AccessLevel accessLevel, String PersName, Integer vacationLeft, Integer overTimeLeft, String username, String email, LocalDate hiredate, String pass, Double weekTime) {
        user = new UserImpl(accessLevel, PersName, vacationLeft, overTimeLeft, username, email, hiredate, pass, weekTime);
    }

    public UserProxy(AccessLevel accessLevel, String PersName, String username, String email, LocalDate hiredate, String pass, Double weekTime) {
        user = new UserImpl(accessLevel, PersName, username, email, hiredate, pass, weekTime);
    }

    @Override
    public AccessLevel getAccessLevel() {
        return user.getAccessLevel();
    }

    @Override
    public String getDisabledString() {
        return user.getDisabledString();
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public LocalDate getHiredate() {
        return user.getHiredate();
    }

    @Override
    public Integer getOverTimeLeft() {
        return user.getOverTimeLeft();
    }

    @Override
    public String getPass() {
        return user.getPass();
    }

    @Override
    public String getPersName() {
        return user.getPersName();
    }

    @Override
    public Integer getUserNr() {
        return user.getUserNr();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Integer getVacationLeft() {
        return user.getVacationLeft();
    }

    @Override
    public Double getWeekTime() {
        return user.getWeekTime();
    }

    @Override
    public boolean isDisabled() {
        return user.isDisabled();
    }

    @Override
    public void setAccessLevel(AccessLevel accessLevel) {
        user.setAccessLevel(accessLevel);
    }

    @Override
    public void setDisabled(boolean disabled) {
        user.setDisabled(disabled);
    }

    @Override
    public void setEmail(String email) {
        user.setEmail(email);
    }

    @Override
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public void setHiredate(LocalDate hiredate) {
        user.setHiredate(hiredate);
    }

    @Override
    public void setOverTimeLeft(Integer overTimeLeft) {
        user.setOverTimeLeft(overTimeLeft);
    }

    @Override
    public void setPass(String pass) {
        user.setPass(pass);
    }

    @Override
    public void setPersName(String PersName) {
        user.setPersName(PersName);
    }

    @Override
    public void setUserNr(Integer userNr) {
        user.setUserNr(userNr);
    }

    @Override
    public void setUsername(String username) {
        user.setUsername(username);
    }

    @Override
    public void setVacationLeft(Integer vacationLeft) {
        user.setVacationLeft(vacationLeft);
    }

    @Override
    public void setWeekTime(Double weekTime) {
        user.setWeekTime(weekTime);
    }

    @Override
    public List<User> getApprover() {
        if (user.getApprover() == null) {
            user.setApprover(USER_DAO.getApprover(user));
        }
        return user.getApprover();
    }

    @Override
    public void setApprover(List<User> approver) {
        user.setApprover(approver);
    }

    @Override
    public Boolean ApproverInitialized() {
        return user.ApproverInitialized();
    }

    @Override
    public boolean equals(Object obj) {
        return user.equals(obj);
    }

    @Override
    public int hashCode() {
        return user.hashCode();
    }

    @Override
    public String toString() {
        return user.toString();
    }

    public static User fromString(String s) {
        s = s.substring(0, s.indexOf("[")) + s.substring(s.indexOf("]"));
        String[] attributes = s.split(",");
        int userNr = Integer.parseInt(attributes[0].substring(attributes[0].indexOf("=") + 1));
        String alName = attributes[2].substring(attributes[2].indexOf("=") + 1);
        AccessLevel al = AccessRightsService.getAccessLevelFromName(alName);
        String persName = attributes[4].substring(attributes[4].indexOf("=") + 1);
        int vacLeft = Integer.parseInt(attributes[5].substring(attributes[5].indexOf("=") + 1));
        int overLeft = Integer.parseInt(attributes[6].substring(attributes[6].indexOf("=") + 1));
        String userName = attributes[7].substring(attributes[7].indexOf("=") + 1);
        String email = attributes[8].substring(attributes[8].indexOf("=") + 1);
        LocalDate hiredate = LocalDate.parse(attributes[9].substring(attributes[9].indexOf("=") + 1));
        String pass = attributes[10].substring(attributes[10].indexOf("=") + 1);
        double weekTime = Double.parseDouble(attributes[11].substring(attributes[11].indexOf("=") + 1));
        boolean disabled = Boolean.valueOf(attributes[12].substring(attributes[12].indexOf("=") + 1));
        return new UserProxy(new UserImpl(userNr, al, persName, vacLeft, overLeft, userName, email, hiredate, pass, weekTime, disabled));
    }

}
