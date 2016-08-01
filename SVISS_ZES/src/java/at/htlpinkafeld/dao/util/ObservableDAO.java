/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.util;

import java.util.Observer;

/**
 *
 * @author Martin Six
 * Used in Base_DAO.java so that a Observer can be registered via interfaces
 */
public interface ObservableDAO {

    public void addObserver(Observer o);

    public void deleteObserver(Observer o);

    public void notifyObservers();
}
