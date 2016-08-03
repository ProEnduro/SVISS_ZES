/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;
import at.htlpinkafeld.dao.interf.AccessLevel_DAO;
import at.htlpinkafeld.dao.interf.User_DAO;
import at.htlpinkafeld.pojo.User;
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
        super.insert(new User(al_DAO.getAccessLevelByID(1), "AdminUser", 0, 0, "admin", "admin@test.at", LocalDate.of(2016, 3, 7), "admin", 38.5));
        super.insert(new User(al_DAO.getAccessLevelByID(2), "ApproverUser", 0, 0, "approver", "approver@test.at", LocalDate.of(2016, 3, 7), "approver", 38.5));
        super.insert(new User(al_DAO.getAccessLevelByID(3), "ReaderUser", 0, 0, "reader", "reader@test.at", LocalDate.of(2016, 3, 7), "reader", 38.5));
        super.insert(new User(al_DAO.getAccessLevelByID(4), "NormalUser", 0, 0, "user", "user@test.at", LocalDate.of(2016, 3, 7), "user", 38.5));
        
        super.insert(new User(al_DAO.getAccessLevelByID(4), "Manuel Lang", 0, 0, "manuel", "manuel.lang@aon.at", LocalDate.of(2016, 4, 10), "manuel", 40.0));
        super.insert(new User(al_DAO.getAccessLevelByID(4), "Matthias Hochhan", 0, 0, "matthias", "matthias.hochhan@gmx.at", LocalDate.of(2016, 2, 14), "matthias", 51.5));
        super.insert(new User(al_DAO.getAccessLevelByID(3), "Franz Schuh", 0, 0, "franz", "franz.schuh@gmail.com", LocalDate.of(2016, 7, 28), "franz", 1.0));
        super.insert(new User(al_DAO.getAccessLevelByID(1), "Scott Whisky", 0, 0, "scott", "scott.whisky@gmx.at", LocalDate.of(2016, 9, 30), "scott", 12.5));
        super.insert(new User(al_DAO.getAccessLevelByID(2), "Smith Hemington", 0, 0, "smith", "smith.hemington@aon.at", LocalDate.of(2016, 3, 1), "smith", 7.0));
        
        super.insert(new User(al_DAO.getAccessLevelByID(1), "Revan Purp", 0, 0, "revan", "revan.purp@gmx.at", LocalDate.of(2016, 8, 26), "revan", 55.5));
        super.insert(new User(al_DAO.getAccessLevelByID(3), "Jen-Hsun Huang", 0, 0, "jen", "jen.huang@nvidia.com", LocalDate.of(2016, 1, 1), "jen", 111.5));
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
        return new User(entity);
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
