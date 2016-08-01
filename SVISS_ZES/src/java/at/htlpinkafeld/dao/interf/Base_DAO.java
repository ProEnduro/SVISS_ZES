/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.interf;

import java.util.List;
import at.htlpinkafeld.dao.util.DAODML_Observable;

/**
 *
 * @author Martin Six
 * @param <T>
 */
public interface Base_DAO<T> extends DAODML_Observable{

    public abstract List<T> getList();

    public abstract void insert(T o);

    public abstract void update(T o);

    public abstract void delete(T o);
}
