/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.service;

import at.htlpinkafeld.dao.factory.DAOFactory;
import at.htlpinkafeld.dao.interf.AccessLevel_DAO;
import at.htlpinkafeld.dao.util.DAODML_Observer;
import at.htlpinkafeld.pojo.AccessLevel;
import edu.emory.mathcs.backport.java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Service which is used to easily check if a {@link AccessLevel} contains a
 * certain permission. via {@link DAODML_Observer} it is automatically notified
 * if a new AccessLevel is added in he database
 *
 * @author Martin Six
 */
public class AccessRightsService implements DAODML_Observer {

    private static final List<String> PERMISSIONS;
    private static final AccessLevel_DAO ALDAO;
    private static final AccessRightsService ARS;

    /**
     * unmodifiale list with all possible {@link AccessLevel}
     */
    public static List<AccessLevel> AccessGroups;

    static {
        PERMISSIONS = new LinkedList<>();
        PERMISSIONS.add("ALL");
        PERMISSIONS.add("VIEW_USERS");
        PERMISSIONS.add("EDIT_USERS");
        PERMISSIONS.add("CREATE_USERS");
        PERMISSIONS.add("EDIT_ACCOUNT");
        PERMISSIONS.add("ACKNOWLEDGE_USERS");
        PERMISSIONS.add("VIEW_ALL_TIMES");
        PERMISSIONS.add("VIEW_ALL_ABSENCES");
        PERMISSIONS.add("INPUT_TIME");
        PERMISSIONS.add("EDIT_HOLIDAY");
        PERMISSIONS.add("EVALUATE_SELF");
        PERMISSIONS.add("EVALUATE_ALL");

        ALDAO = DAOFactory.getDAOFactory().getAccessLevelDAO();
        ARS = new AccessRightsService();
        ALDAO.addObserver(ARS);

        AccessGroups = Collections.unmodifiableList(ALDAO.getList());
    }

    private AccessRightsService() {
    }

    /**
     * Manually reloads all available AccessLevels
     */
    public static void reloadAccessGroups() {
        AccessGroups = Collections.unmodifiableList(ALDAO.getList());
    }

    /**
     * checks the AccessLevel if it contains a certain Permission or the "ALL"
     * Permission
     *
     * @param al
     * @param neededPermission
     * @return true if Permission is contained, false otherwise
     */
    public static boolean checkPermission(AccessLevel al, String neededPermission) {
        return al.containsPermission(neededPermission.toUpperCase()) || al.containsPermission("ALL");
    }

    /**
     * Method used to create a AccessLevel from the AccessLevel name
     *
     * @param accessLevelString
     * @return AccessLevel with this name or null if it doesn't exist
     */
    public static AccessLevel getAccessLevelFromName(String accessLevelString) {
        for (AccessLevel al : AccessGroups) {
            if (al.getAccessLevelName().toLowerCase().contentEquals(accessLevelString.toLowerCase())) {
                return al;
            }
        }
        return null;
    }

    @Override
    public void notifyObserver() {
        reloadAccessGroups();
    }

}
