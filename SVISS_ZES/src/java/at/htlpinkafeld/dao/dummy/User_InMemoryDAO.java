/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.interf.AccessLevel_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.AccessLevel;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserProxy;
import at.htlpinkafeld.service.PasswordEncryptionService;
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
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(1), "AdminUser", 21, 36 * 60, "admin", "admin@noplan.at", LocalDate.of(2016, 3, 7), PasswordEncryptionService.digestPassword("admin"), 14.0));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(2), "ApproverUser", 21, 60 * 60, "approver", "blaumeux.sn@gmail.com", LocalDate.of(2016, 3, 7), PasswordEncryptionService.digestPassword("approver"), 33.5));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(3), "ReaderUser", 10, 54 * 60, "reader", "reader@test.at", LocalDate.of(2016, 3, 7), PasswordEncryptionService.digestPassword("reader"), 22.5));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(4), "NormalUser", 19, 30 * 60, "user", "user@test.at", LocalDate.of(2016, 3, 7), PasswordEncryptionService.digestPassword("user"), 17.5));

        super.insert(new UserProxy(al_DAO.getAccessLevelByID(4), "Manuel Lang", 20, 24 * 60, "manuel", "manuel.lang@aon.at", LocalDate.of(2016, 4, 10), PasswordEncryptionService.digestPassword("manuel"), 40.0));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(4), "Matthias Hochhan", 12, 48 * 60, "matthias", "matthias.hochhan@gmx.at", LocalDate.of(2016, 2, 14), PasswordEncryptionService.digestPassword("matthias"), 51.5));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(3), "Franz Schuh", 12, 42 * 60, "franz", "franz.schuh@gmail.com", LocalDate.of(2016, 7, 28), PasswordEncryptionService.digestPassword("franz"), 1.0));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(1), "Scott Whisky", 5, 30 * 60, "scott", "scott.whisky@gmx.at", LocalDate.of(2016, 9, 30), PasswordEncryptionService.digestPassword("scott"), 12.5));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(2), "Smith Hemington", 1, 0 * 60, "smith", "smith.hemington@aon.at", LocalDate.of(2016, 3, 1), PasswordEncryptionService.digestPassword("smith"), 7.0));

        super.insert(new UserProxy(al_DAO.getAccessLevelByID(1), "Revan Purp", 11, 12 * 60, "revan", "revan.purp@gmx.at", LocalDate.of(2016, 8, 26), PasswordEncryptionService.digestPassword("revan"), 55.5));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(3), "Jen-Hsun Huang", 12, 54 * 60, "jen", "jen.huang@nvidia.com", LocalDate.of(2016, 1, 1), PasswordEncryptionService.digestPassword("jen"), 111.5));

        super.insert(new UserProxy(al_DAO.getAccessLevelByID(4), "Meg Hang", 9, 18 * 60, "meg", "meg.hang@gmx.at", LocalDate.of(2016, 8, 21), "meg", 0.0));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(2), "Hodor Hold_the_Door", 10, 12 * 60, "Hodor", "hodor.door@gmx.at", LocalDate.of(2016, 1, 18), "hodor", 10.0));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(3), "Bran O'Neal", 2, 6 * 60, "bran", "bran.oneal@gmx.at", LocalDate.of(2016, 9, 26), "bran", 44.5));

        super.insert(new UserProxy(al_DAO.getAccessLevelByID(4), "Hans Steifer", 17, 36 * 60, "hans", "hans.steifer@gmail.com", LocalDate.of(2016, 6, 19), "hans", 12.0));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(1), "Johannes Engmot", 4, 48 * 60, "johannes", "johannes.engmot@aon.at", LocalDate.of(2016, 5, 2), "johannes", 22.5));
        super.insert(new UserProxy(al_DAO.getAccessLevelByID(4), "John Wayne", 15, 30 * 60, "john", "john.wayne@gmx.at", LocalDate.of(2016, 4, 14), "john", 33.5));
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
        List<User> userL = new ArrayList<>();
        for (User u : getList()) {
            if (u.isDisabled() == disabled) {
                userL.add(clone(u));
            }
        }
        return userL;
    }

    @Override
    public List<User> getApprover(User user) {
        List<User> approver;
        if (user.ApproverInitialized()) {
            approver = getList().get(getList().indexOf(user)).getApprover();
            return approver;
        } else {
            approver = new ArrayList<>();
            user.setApprover(approver);
            update(user);
            return approver;
        }
    }

    @Override
    public void updateApproverOfUser(User user) {
        update(user);
    }

    @Override
    public void removeApprover(User approver) {
        for (User u : getList()) {
            u.getApprover().remove(approver);
        }
    }

    @Override
    public List<User> getUserByAccessLevel(AccessLevel accessLevel) {
        List<User> userL = new ArrayList<>();
        for (User u : getList()) {
            if (u.getAccessLevel().equals(accessLevel)) {
                userL.add(clone(u));
            }
        }
        return userL;
    }

    @Override
    public User getUserByEmail(String email) {
        for (User u : super.getList()) {
            if (u.getEmail().contentEquals(email)) {
                return clone(u);
            }
        }
        return null;
    }

}
