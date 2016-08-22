/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.interf;

import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserHistoryEntry;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import org.apache.tomcat.jni.Local;

/**
 *
 * @author Martin Six
 */
public interface UserHistory_DAO extends Base_DAO<UserHistoryEntry> {

    public List<UserHistoryEntry> getUserHistoryEntrysByUser(User u);

    public List<UserHistoryEntry> getUserHistoryEntrysByUser_BetweenDates(User u, LocalDate startDate, LocalDate endDate);
}
