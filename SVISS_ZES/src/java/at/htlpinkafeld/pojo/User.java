/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public interface User extends Serializable {

    AccessLevel getAccessLevel();

    String getDisabledString();

    String getEmail();

    LocalDate getHiredate();

    int getOverTimeLeft();

    String getPass();

    String getPersName();

    int getUserNr();

    String getUsername();

    int getVacationLeft();

    Double getWeekTime();

    List<User> getApprover();

    boolean isDisabled();

    void setAccessLevel(AccessLevel accessLevel);

    void setDisabled(boolean disabled);

    void setEmail(String email);

    void setHiredate(LocalDate hiredate);

    void setOverTimeLeft(int overTimeLeft);

    void setPass(String pass);

    void setPersName(String PersName);

    void setUserNr(int userNr);

    void setUsername(String username);

    void setVacationLeft(int vacationLeft);

    void setWeekTime(Double weekTime);

    void setApprover(List<User> approver);

    boolean ApproverInitialized();
}
