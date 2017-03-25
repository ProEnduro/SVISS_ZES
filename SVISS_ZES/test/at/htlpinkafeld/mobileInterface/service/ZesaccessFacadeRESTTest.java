/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.dao.jdbc.ConnectionManager;
import at.htlpinkafeld.mobileInterface.authorization.Credentials;
import at.htlpinkafeld.pojo.AccessLevel;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Martin Six
 */
public class ZesaccessFacadeRESTTest {

    private WebTarget webTarget;
    private Client client;
    private String token;
    private static final String BASE_URI = "http://localhost:8084/SVISS_ZES/webresources";

    public ZesaccessFacadeRESTTest() {
        ConnectionManager.setDebugInstance(true);
    }

    @AfterClass
    public static void setUpClass() throws Exception {
        ConnectionManager.setDebugInstance(false);
    }

    @Before
    public void setUp() {
        client = javax.ws.rs.client.ClientBuilder.newClient().property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);

        WebTarget authTarget = client.target(BASE_URI).path("authentication");
        Credentials credentials = new Credentials("admin", "admin");
        token = authTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(credentials, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class).readEntity(String.class);

        webTarget = client.target(BASE_URI).path("zesaccess");
    }

    @After
    public void tearDown() {
        client.close();
        ConnectionManager.rollback();
    }

    /**
     * Test of findAll method, of class ZesaccessFacadeREST.
     */
    @Test
    public void testFindAll() {
        System.out.println("findAll");
        Response result;
        List<AccessLevel> l;

        result = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        l = (List<AccessLevel>) result.readEntity(new GenericType<List<AccessLevel>>() {
        });

        Assert.assertFalse(l.isEmpty());

        l.forEach((o) -> {
            o.toString();
        });
    }

    /**
     * Test of various methods, of class ZesaccessFacadeREST.
     */
    @Test
    public void testCreateEditRemove() {
        System.out.println("create");
        AccessLevel result;
        Response response;
        List<AccessLevel> accessLevels;
        AccessLevel al = new AccessLevel("NoPlan", new LinkedList());
        Response res = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).post(Entity.json(al));
        result = res.readEntity(AccessLevel.class);

        Assert.assertNotEquals("Assert if create and key-generation worked", al.getAccessLevelID(), result.getAccessLevelID());

        al = new AccessLevel(result);
        al.setAccessLevelName("%&?$§!NoPlan%&?$§!%&?$§!");
        al.getPermissions().add("ALL");

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).put(Entity.json(al));

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        accessLevels = (List<AccessLevel>) response.readEntity((GenericType) new GenericType<List<AccessLevel>>() {
        });

        for (AccessLevel accessLevel : accessLevels) {
            if (accessLevel.getAccessLevelID().equals(al.getAccessLevelID())) {
                result = accessLevel;
            }
        }

        Assert.assertEquals("Assert if edit worked", al, result);

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build("PATCH", Entity.json(al)).invoke();

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        accessLevels = (List<AccessLevel>) response.readEntity((GenericType) new GenericType<List<AccessLevel>>() {
        });

        Assert.assertFalse("Assert if delete worked", accessLevels.contains(al));
    }

}
