/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.util;

/**
 *
 * @author Martin Six Used in Base_DAO.java so that a Observer can be registered
 * via interfaces
 */
public interface DAODML_Observable {

    public void addObserver(DAODML_Observer o);

    public void deleteObserver(DAODML_Observer o);

    public void notifyObservers();
}
