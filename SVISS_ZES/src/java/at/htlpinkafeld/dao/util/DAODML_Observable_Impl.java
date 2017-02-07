/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementation for {@link DAODML_Observable} which provides its basic
 * implementation
 *
 * @author Martin Six
 */
public class DAODML_Observable_Impl implements DAODML_Observable {

    List<DAODML_Observer> observers = new LinkedList<>();

    @Override
    public void addObserver(DAODML_Observer o) {
        observers.add(o);
    }

    @Override
    public void deleteObserver(DAODML_Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (DAODML_Observer o : observers) {
            o.notifyObserver();
        }
    }

}
