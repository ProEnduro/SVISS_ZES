/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import at.htlpinkafeld.mobileInterface.service.util.LocalDateAdapter;
import java.time.LocalDate;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Martin Six
 */
public interface User {

    Boolean ApproverInitialized();

    AccessLevel getAccessLevel();

    List<User> getApprover();

    String getDisabledString();

    String getEmail();

    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    LocalDate getHiredate();

    Integer getOverTimeLeft();

    String getPass();

    String getPersName();

    Integer getUserNr();

    String getUsername();

    Integer getVacationLeft();

    Double getWeekTime();

    boolean isDisabled();

    void setAccessLevel(AccessLevel accessLevel);

    void setApprover(List<User> approver);

    void setDisabled(boolean disabled);

    void setEmail(String email);

    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    void setHiredate(LocalDate hiredate);

    void setOverTimeLeft(Integer overTimeLeft);

    void setPass(String pass);

    void setPersName(String PersName);

    void setUserNr(Integer userNr);

    void setUsername(String username);

    void setVacationLeft(Integer vacationLeft);

    void setWeekTime(Double weekTime);
    
}
