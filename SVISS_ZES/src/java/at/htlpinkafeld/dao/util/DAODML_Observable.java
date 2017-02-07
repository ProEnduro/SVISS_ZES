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

    /**
     * registers the {@link DAODML_Observer} o to this object
     *
     * @param o
     */
    public void addObserver(DAODML_Observer o);

    /**
     * removes the {@link DAODML_Observer} o from the observers of this object
     *
     * @param o
     */
    public void deleteObserver(DAODML_Observer o);

    /**
     * notifies all registered {@link DAODML_Observer}
     */
    public void notifyObservers();
}
