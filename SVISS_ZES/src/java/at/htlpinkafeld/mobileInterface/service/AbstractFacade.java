/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.dao.interf.Base_DAO;
import java.util.List;

/**
 *
 * @author Martin Six
 * @param <T>
 */
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract Base_DAO<T> getDAO();

    public T create(T entity) {
        getDAO().insert(entity);
        return entity;
    }

    public void edit(T entity) {
        getDAO().update(entity);
    }

    public void remove(T entity) {
        getDAO().delete(entity);
    }

    public List<T> findAll() {
        return getDAO().getList();
    }
}
