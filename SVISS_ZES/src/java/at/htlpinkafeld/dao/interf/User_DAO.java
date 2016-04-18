/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.interf;

import at.htlpinkafeld.pojo.User;

/**
 *
 * @author Martin Six
 */
public interface User_DAO extends Base_DAO<User> {

    public abstract User getUser(int userNr);

    public abstract User getUserByUsername(String username);
}
