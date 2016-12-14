/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.mobileInterface.authorization.Secured;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public interface RestInterface<T> {

    public T create(String jsonEntity);

    public void edit(String jsonEntity);

    public void remove(String jsonEntity);

    public List<T> findAll();
}
