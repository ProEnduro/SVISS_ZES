/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.Base_DAO;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public abstract class Base_DummyDAO<T> implements Base_DAO<T> {

    private final List<T> oList;

    public Base_DummyDAO(List<T> oList) {
        this.oList = oList;
    }

    @Override
    public List<T> getList() {
        return oList;
    }

    @Override
    public void insert(T o) {
        oList.add(o);
    }

    @Override
    public void update(T o) {
        oList.set(oList.indexOf(o), o);
    }

    @Override
    public void delete(T o) {
        oList.remove(o);
    }

}
