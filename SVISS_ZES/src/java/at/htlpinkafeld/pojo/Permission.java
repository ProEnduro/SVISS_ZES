/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.pojo;

/**
 *
 * @author Martin Six
 */
public class Permission {

    private int permID;
    private String permName;

    public Permission(Permission p) {
        this.permID = p.permID;
        this.permName = p.permName;
    }

    public Permission(int permID, String permName) {
        this.permID = permID;
        this.permName = permName;
    }

    public int getPermID() {
        return permID;
    }

    public void setPermID(int permID) {
        this.permID = permID;
    }

    public String getPermName() {
        return permName;
    }

    public void setPermName(String permName) {
        this.permName = permName;
    }

    @Override
    public String toString() {
        return "Permission{" + "permID=" + permID + ", permName=" + permName + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.permID;
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
        final Permission other = (Permission) obj;
        if (this.permID != other.permID) {
            return false;
        }
        return true;
    }

}
