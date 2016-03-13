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
public abstract class User_DAO {

    protected static String PERSNR = "PERSNR";
    protected static String ACCESSLEVELID = "ACCESSLEVELID";
    protected static String PERSNAME = "PERSNAME";
    protected static String VACATIONLEFT = "VACATIONLEFT";
    protected static String OVERTIMELEFT = "OVERTIMELEFT";
    protected static String USERNAME = "USERNAME";
    protected static String EMAIL = "EMAIL";
    protected static String HIREDATE = "HIREDATE";
    protected static String PASSWORD = "PASSWORD";
    protected static String WEEKTIME = "WEEKTIME";

    public abstract List<User> getUserList();

    public abstract User getUser(int persnr);

    public abstract void insertUser(User u);

    public abstract void updateUser(User u);

    public abstract void deleteUser(int persnr);

}
