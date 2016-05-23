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

    private static User_DAO userdao;

    public BenutzerverwaltungService() {
    }

    static public List<User> getUserList() {
        userdao = DAOFactory.getDAOFactory().getUserDAO();
        return userdao.getList();
    }

    static public void insertUser(User u) {
        userdao = DAOFactory.getDAOFactory().getUserDAO();
        userdao.insert(u);
    }

    static public void updateUser(User u) {
        userdao = DAOFactory.getDAOFactory().getUserDAO();
        userdao.update(u);
    }
}
