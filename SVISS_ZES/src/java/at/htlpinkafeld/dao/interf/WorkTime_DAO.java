/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.interf;

import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public interface WorkTime_DAO extends Base_DAO<WorkTime> {

    public abstract List<WorkTime> getWorkTimesByUser(User u);
}
