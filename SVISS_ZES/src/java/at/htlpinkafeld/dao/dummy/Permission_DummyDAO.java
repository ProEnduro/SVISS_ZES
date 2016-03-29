/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao.dummy;

import at.htlpinkafeld.dao.Permission_DAO;
import at.htlpinkafeld.pojo.Permission;
import java.util.LinkedList;

/**
 *
 * @author Martin Six
 */
public class Permission_DummyDAO extends Base_DummyDAO<Permission> implements Permission_DAO {

    public Permission_DummyDAO() {
        super(new LinkedList<>());

    }

}
