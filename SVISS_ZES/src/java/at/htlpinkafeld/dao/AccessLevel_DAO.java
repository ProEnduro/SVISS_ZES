/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.dao;

import at.htlpinkafeld.pojo.AccessLevel;

/**
 *
 * @author Martin Six
 */
public interface AccessLevel_DAO extends Base_DAO<AccessLevel> {

    public abstract AccessLevel getAccessLevelByID(int accessLevelId);
}
