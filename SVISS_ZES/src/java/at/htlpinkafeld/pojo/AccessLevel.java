/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Martin Six
 */
public class AccessLevel {

    private int accessLevelID;
    private String accessLevelName;
    private List<Permission> permissions;

    public AccessLevel() {
        accessLevelID = -1;
    }

    public AccessLevel(AccessLevel al) {
        this.accessLevelID = al.accessLevelID;
        this.accessLevelName = al.accessLevelName;
        this.permissions = al.permissions;
    }

    @Deprecated
    public AccessLevel(int accessLevelID, String accessLevelName, List<Permission> permissions) {
        this.accessLevelID = accessLevelID;
        this.accessLevelName = accessLevelName;
        this.permissions = permissions;
    }

    public AccessLevel(String accessLevelName, List<Permission> permissions) {
        this();
        this.accessLevelName = accessLevelName;
        this.permissions = permissions;
    }

    public int getAccessLevelID() {
        return accessLevelID;
    }

    public void setAccessLevelID(int accessLevelID) {
        this.accessLevelID = accessLevelID;
    }

    public String getAccessLevelName() {
        return accessLevelName;
    }

    public void setAccessLevelName(String accessLevelName) {
        this.accessLevelName = accessLevelName;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean containsPermission(Permission p) {
        return permissions.contains(p);
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
