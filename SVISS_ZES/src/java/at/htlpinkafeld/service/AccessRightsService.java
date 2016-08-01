/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.AccessLevel_DAO;
import at.htlpinkafeld.pojo.AccessLevel;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class AccessRightsService {

    private static final List<String> PERMISSIONS;
    private static final AccessLevel_DAO ALDAO;

    private static List<AccessLevel> AccessGroups;

    static {
        PERMISSIONS = new LinkedList<>();
        PERMISSIONS.add("EDIT_USERS");
        PERMISSIONS.add("VIEW_USERS");
        PERMISSIONS.add("VIEW_CALENDAR");
        PERMISSIONS.add("INPUT_TIME");

        ALDAO = DAOFactory.getDAOFactory().getAccessLevelDAO();
        AccessGroups = ALDAO.getList();
    }

    /**
     * This function has to be called when the AccessLevel table was changed
     */
    public static void reloadAccessGroups() {
        AccessGroups = ALDAO.getList();
    }

    public static boolean checkPermission(AccessLevel al, String neededPermission) {
        if (al.containsPermission(neededPermission)) {
            return true;
        } else {
            for (String p : al.getPermissions()) {
                return checkPermission(getAccessLevelFromName(p), neededPermission);
            }
        }
        return false;
    }

    private static AccessLevel getAccessLevelFromName(String accessLevelString) {
        for (AccessLevel al : AccessGroups) {
            if (al.getAccessLevelName().contentEquals(accessLevelString)) {
                return al;
            }
        }
        return null;
    }

}
