/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.AccessLevel_DAO;
import at.htlpinkafeld.dao.User_DAO;
import at.htlpinkafeld.pojo.User;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 *
 * @author Martin Six
 */
public class User_DummyDAO extends Base_DummyDAO<User> implements User_DAO {

    public User_DummyDAO() {
        super(new ArrayList<>());
        AccessLevel_DAO al_DAO = new AccessLevel_DummyDAO();
        super.insert(new User(1, al_DAO.getAccessLevelByID(1), "AdminUser", "admin", "admin@test.at", new GregorianCalendar(2016, 3, 7).getTime(), "admin", 38.5));
        super.insert(new User(2, al_DAO.getAccessLevelByID(2), "ApproverUser", "approver", "approver@test.at", new GregorianCalendar(2016, 3, 7).getTime(), "approver", 38.5));
        super.insert(new User(3, al_DAO.getAccessLevelByID(3), "ReaderUser", "reader", "reader@test.at", new GregorianCalendar(2016, 3, 7).getTime(), "reader", 38.5));
        super.insert(new User(4, al_DAO.getAccessLevelByID(4), "NormalUser", "user", "user@test.at", new GregorianCalendar(2016, 3, 7).getTime(), "user", 38.5));
    }

    @Override
    public User getUser(int persnr) {
        for (User u : super.getList()) {
            if (u.getPersNr() == persnr) {
                return u;
            }
        }
        return null;
    }

}
