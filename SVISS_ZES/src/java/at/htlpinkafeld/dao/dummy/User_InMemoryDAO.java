/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;
import at.htlpinkafeld.dao.interf.AccessLevel_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserProxy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class User_InMemoryDAO extends Base_InMemoryDAO<User> implements User_DAO {

    protected User_InMemoryDAO() {
        super(new ArrayList<>());
        AccessLevel_DAO al_DAO = new AccessLevel_InMemoryDAO();
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(1), "AdminUser", 21, 36, "admin", "admin@test.at", LocalDate.of(2016, 3, 7), "admin", 14.0));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(2), "ApproverUser", 21, 60, "approver", "approver@test.at", LocalDate.of(2016, 3, 7), "approver", 33.5));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(3), "ReaderUser", 10, 54, "reader", "reader@test.at", LocalDate.of(2016, 3, 7), "reader", 22.5));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(4), "NormalUser", 19, 30, "user", "user@test.at", LocalDate.of(2016, 3, 7), "user", 17.5));
        
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(4), "Manuel Lang", 20, 24, "manuel", "manuel.lang@aon.at", LocalDate.of(2016, 4, 10), "manuel", 40.0));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(4), "Matthias Hochhan", 12, 48, "matthias", "matthias.hochhan@gmx.at", LocalDate.of(2016, 2, 14), "matthias", 51.5));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(3), "Franz Schuh", 12, 42, "franz", "franz.schuh@gmail.com", LocalDate.of(2016, 7, 28), "franz", 1.0));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(1), "Scott Whisky", 5, 30, "scott", "scott.whisky@gmx.at", LocalDate.of(2016, 9, 30), "scott", 12.5));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(2), "Smith Hemington", 1, 0, "smith", "smith.hemington@aon.at", LocalDate.of(2016, 3, 1), "smith", 7.0));
        
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(1), "Revan Purp", 11, 12, "revan", "revan.purp@gmx.at", LocalDate.of(2016, 8, 26), "revan", 55.5));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(3), "Jen-Hsun Huang", 12, 54, "jen", "jen.huang@nvidia.com", LocalDate.of(2016, 1, 1), "jen", 111.5));
        
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(4), "Meg Hang", 9, 18, "meg", "meg.hang@gmx.at", LocalDate.of(2016, 8, 21), "meg", 0.0));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(2), "Hodor Hold_the_Door", 10, 12, "Hodor", "hodor.door@gmx.at", LocalDate.of(2016, 1, 18), "hodor", 10.0));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(3), "Bran O'Neal", 2, 6, "Bran", "bran.oneal@gmx.at", LocalDate.of(2016, 9, 26), "bran", 44.5));
        
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(4), "Hans Steifer", 17, 36, "Hans", "hans.steifer@gmail.com", LocalDate.of(2016, 6, 19), "hans", 12.0));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(1), "Johannes Engmot", 4, 48, "Johannes", "johannes.engmot@aon.at", LocalDate.of(2016, 5, 2), "johannes", 22.5));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(4), "John Wayne", 15, 30, "john", "john.wayne@gmx.at", LocalDate.of(2016, 4, 14), "john", 33.5));
        
        
        
    }

    @Override
    public User getUser(int persnr) {
        for (User u : super.getList()) {
            if (u.getUserNr() == persnr) {
                return clone(u);
            }
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        for (User u : super.getList()) {
            if (u.getUsername().contentEquals(username)) {
                return clone(u);
            }
        }
        return null;
    }

    @Override
    protected User clone(User entity) {
        return new UserProxy(entity);
    }

    @Override
    protected void setID(User entity, int id) {
        entity.setUserNr(id);
    }

    @Override
    public List<User> getUserByDisabled(Boolean disabled) {
        List<User> userL = getList();
        for (User u : userL) {
            if (u.isDisabled() != disabled) {
                userL.remove(u);
            }
        }
        return userL;
    }

}
