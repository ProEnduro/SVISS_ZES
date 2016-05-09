/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.interf;

import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.User;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public interface Absence_DAO extends Base_DAO<Absence> {

    public abstract List<Absence> getAbsenceByUser(User u);
}