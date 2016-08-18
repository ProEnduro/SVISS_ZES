/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.interf;

import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import java.time.DayOfWeek;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public interface SollZeiten_DAO extends Base_DAO<SollZeit> {

    public abstract List<SollZeit> getSollZeitenByUser(User u);
    
    public abstract SollZeit getSollZeitenByUser_DayOfWeek(User u, DayOfWeek d);
}
