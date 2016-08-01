/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.interf;

import at.htlpinkafeld.dao.util.ObservableDAO;
import java.util.List;

/**
 *
 * @author Martin Six
 * @param <T>
 */
public interface Base_DAO<T> extends ObservableDAO {

    public abstract List<T> getList();

    public abstract void insert(T o);

    public abstract void update(T o);

    public abstract void delete(T o);
}
