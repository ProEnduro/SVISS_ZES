/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.Base_DAO_Observable;
import at.htlpinkafeld.dao.util.DAOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin Six
 * @param <T>
 */
public abstract class Base_InMemoryDAO<T> extends Base_DAO_Observable<T> {

    private final List<T> oList;
    private int ID = 1;

    protected Base_InMemoryDAO(List<T> oList) {
        this.oList = oList;
    }

    @Override
    public List<T> getList() {
        List<T> retList = new ArrayList<>();
        oList.forEach((entity) -> {
            retList.add(clone(entity));
        });
        return retList;
    }

    @Override
    public void insert(T o) {
        setID(o, ID++);
        if (oList.contains(o)) {
            throw new DAOException("Duplicate value on index");
        }
        oList.add(clone(o));
        notifyObservers();
    }

    @Override
    public void update(T o) {
        int idx = oList.indexOf(o);
        if (idx != -1) {
            oList.set(idx, clone(o));
            notifyObservers();
        }
    }

    @Override
    public void delete(T o) {
        oList.remove(o);
        notifyObservers();
    }

    protected abstract T clone(T entity);

    protected abstract void setID(T entity, int id);
}
