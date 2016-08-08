/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import java.time.LocalDate;

/**
 *
 * @author Martin Six
 */
public class UserProxy implements User {

    private User user;

    public UserProxy() {
        user = new UserImpl();
    }

    public UserProxy(User u) {
        user = new UserImpl(u);
    }

    @Deprecated
    public UserProxy(int userNr, AccessLevel accessLevel, String PersName, int vacationLeft, int overTimeLeft, String username, String email, LocalDate hiredate, String pass, Double weekTime, boolean disabled) {
        user = new UserImpl(userNr, accessLevel, PersName, vacationLeft, overTimeLeft, username, email, hiredate, pass, weekTime, disabled);
    }

    public UserProxy(AccessLevel accessLevel, String PersName, int vacationLeft, int overTimeLeft, String username, String email, LocalDate hiredate, String pass, Double weekTime) {
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
    public LocalDate getHiredate() {
        return user.getHiredate();
    }

    @Override
    public int getOverTimeLeft() {
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
    public int getUserNr() {
        return user.getUserNr();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public int getVacationLeft() {
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
    public void setHiredate(LocalDate hiredate) {
        user.setHiredate(hiredate);
    }

    @Override
    public void setOverTimeLeft(int overTimeLeft) {
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
    public void setUserNr(int userNr) {
        user.setUserNr(userNr);
    }

    @Override
    public void setUsername(String username) {
        user.setUsername(username);
    }

    @Override
    public void setVacationLeft(int vacationLeft) {
        user.setVacationLeft(vacationLeft);
    }

    @Override
    public void setWeekTime(Double weekTime) {
        user.setWeekTime(weekTime);
    }

}
