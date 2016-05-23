/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.beans;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.pojo.User;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class DBBean {

    List<User> userL;

    public DBBean() {
        userL = DAOFactory.getDAOFactory().getUserDAO().getList();
    }

    public List<User> getUserL() {
        return userL;
    }

    public void setUserL(List<User> userL) {
        this.userL = userL;
    }

}
