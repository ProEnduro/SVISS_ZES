/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Martin Six
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(at.htlpinkafeld.mobileInterface.authorization.AuthenticationEndpoint.class);
        resources.add(at.htlpinkafeld.mobileInterface.authorization.AuthenticationFilter.class);
        resources.add(at.htlpinkafeld.mobileInterface.service.AbsenceFacadeREST.class);
        resources.add(at.htlpinkafeld.mobileInterface.service.AbsencetypeFacadeREST.class);
        resources.add(at.htlpinkafeld.mobileInterface.service.EventFacadeRest.class);
        resources.add(at.htlpinkafeld.mobileInterface.service.HolidayFacadeREST.class);
        resources.add(at.htlpinkafeld.mobileInterface.service.SollzeitenFacadeREST.class);
        resources.add(at.htlpinkafeld.mobileInterface.service.UserFacadeREST.class);
        resources.add(at.htlpinkafeld.mobileInterface.service.UserholidayovertimehistoryFacadeREST.class);
        resources.add(at.htlpinkafeld.mobileInterface.service.WorktimeFacadeREST.class);
        resources.add(at.htlpinkafeld.mobileInterface.service.ZesaccessFacadeREST.class);
    }

}
