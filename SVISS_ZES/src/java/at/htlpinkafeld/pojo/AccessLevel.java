/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import java.util.Map;

/**
 *
 * @author Martin Six
 */
public class AccessLevel {
    private final int accessLevelID;
    private final String accessLevelName;
    //the Integer is the id of the Permission
    private final Map<Integer, Permission> permissions;

    public AccessLevel(int accessLevelID, String accessLevelName, Map<Integer, Permission> permissions) {
        this.accessLevelID = accessLevelID;
        this.accessLevelName = accessLevelName;
        this.permissions = permissions;
    }

    public int getAccessLevelID() {
        return accessLevelID;
    }

    public String getAccessLevelName() {
        return accessLevelName;
    }

    public Map<Integer, Permission> getPermissions() {
        return permissions;
    }

    @Override
    public String toString() {
        return "AccessLevel{" + "accessLevelID=" + accessLevelID + ", accessLevelName=" + accessLevelName + ", permissions=" + permissions + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + this.accessLevelID;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AccessLevel other = (AccessLevel) obj;
        if (this.accessLevelID != other.accessLevelID) {
            return false;
        }
        return true;
    }
    
    
}
