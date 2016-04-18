/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.interf;

import at.htlpinkafeld.pojo.Permission;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public interface Permission_DAO extends Base_DAO<Permission> {

    public abstract List<Permission> getPermissionsByAccessLevelID(int id);
}
