/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.dao.jdbc.ConnectionManager;
import at.htlpinkafeld.mobileInterface.authorization.Credentials;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserProxy;
import at.htlpinkafeld.service.AccessRightsService;
import java.time.LocalDate;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
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
public class UserFacadeRESTTest {

    private WebTarget webTarget;
    private Client client;
    private String token;
    private static final String BASE_URI = "http://localhost:8084/SVISS_ZES/webresources";

    public UserFacadeRESTTest() {
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

        webTarget = client.target(BASE_URI).path("user");
    }

    @After
    public void tearDown() {
        client.close();
        ConnectionManager.rollback();
    }

    /**
     * Test of findAll method, of class UserFacadeREST.
     */
    @Test
    public void testFindAll() {
        System.out.println("findAll");
        Response result;
        List<UserProxy> l;

        result = webTarget.request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        l = (List<UserProxy>) result.readEntity(new GenericType<List<UserProxy>>() {
        });

        Assert.assertFalse(l.isEmpty());

        l.forEach((o) -> {
            o.toString();
        });
    }

    /**
     * Test of create method, of class UserFacadeREST.
     */
    @Test
    public void testCreateEditRemove() {
        System.out.println("create");
        User createResult;
        User editResult = null;
        User startUser;
        User u;
        Response response;
        List<User> userL;
        startUser = new UserProxy(AccessRightsService.getAccessLevelFromName("admin"), "x", "x", "xmail", LocalDate.of(1998, 7, 9), "x", 0.0);

        createResult = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).post(Entity.json(startUser)).readEntity(UserProxy.class);

        u = new UserProxy(createResult);
        u.setUsername("awdawdawd!NoPlan!wadwd");

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).put(Entity.json(u));

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        userL = (List<User>) response.readEntity((GenericType) new GenericType<List<UserProxy>>() {
        });

        for (User user : userL) {
            if (user.getUsername().contentEquals(u.getUsername())) {
                editResult = user;
            }
        }

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build("PATCH", Entity.json(u)).invoke();

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        userL = (List<User>) response.readEntity((GenericType) new GenericType<List<UserProxy>>() {
        });

        Assert.assertNotEquals("Check create and the auto-created key", startUser.getUserNr(), createResult.getUserNr());
        Assert.assertEquals("Check if the edit worked", u, editResult);
        Assert.assertFalse("Check remove", userL.contains(u));
    }

}
