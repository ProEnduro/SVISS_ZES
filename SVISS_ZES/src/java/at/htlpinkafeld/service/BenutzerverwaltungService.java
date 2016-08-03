/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.User;
import java.util.List;

/**
 *
 * @author msi
 */
public class BenutzerverwaltungService {

    private final static User_DAO userdao;
    
    static{
        
        userdao = DAOFactory.getDAOFactory().getUserDAO();
        
    }

    public BenutzerverwaltungService() {
    }

    static public List<User> getUserList() {
        return userdao.getList();
    }

    static public void insertUser(User u) {
        userdao.insert(u);
    }

    static public void updateUser(User u) {
        userdao.update(u);
    }
    
    public static User getUser(int id){
        return userdao.getUser(id);
    }
}
