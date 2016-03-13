/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao;

import at.htlpinkafeld.pojo.User;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public interface User_DAO {

    public List<User> getUserList();

    public User getUser(int persnr);

    public void insertUser(User u);

    public void updateUser(User u);

    public void deleteUser(int persnr);

}
