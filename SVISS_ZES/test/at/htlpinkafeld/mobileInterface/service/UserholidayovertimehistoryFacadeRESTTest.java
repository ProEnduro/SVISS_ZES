/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.dao.jdbc.ConnectionManager;
import at.htlpinkafeld.mobileInterface.authorization.Credentials;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.UserHistoryEntry;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
public class UserholidayovertimehistoryFacadeRESTTest {

    private WebTarget webTarget;
    private Client client;
    private String token;
    private static final String BASE_URI = "http://localhost:8084/SVISS_ZES/webresources";

    public UserholidayovertimehistoryFacadeRESTTest() {
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

        webTarget = client.target(BASE_URI).path("userholidayovertimehistory");
    }

    @After
    public void tearDown() {
        client.close();
        ConnectionManager.rollback();
    }

    /**
     * Test of findAll method, of class UserholidayovertimehistoryFacadeREST.
     */
    @Test
    public void testFindAll() {
        System.out.println("findAll");
        Response result;
        List<UserHistoryEntry> l;

        result = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        l = (List<UserHistoryEntry>) result.readEntity(new GenericType<List<UserHistoryEntry>>() {
        });

        Assert.assertFalse("Check for getList being empty", l.isEmpty());

        for (Object o : l) {
            o.toString();
        }
    }

    /**
     * Test of various methods, of class UserholidayovertimehistoryFacadeREST.
     */
    @Test
    public void testCreateEditRemove() {
        System.out.println("create");
        UserHistoryEntry result;
        Response response;
        List<UserHistoryEntry> historyEntrys;
        User u = new User(BenutzerverwaltungService.getUserList().get(0));
        UserHistoryEntry uhe = new UserHistoryEntry(LocalDateTime.now(), u, u.getOverTimeLeft(), u.getVacationLeft());
        Response res = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).post(Entity.json(uhe));
        result = res.readEntity(UserHistoryEntry.class);
        Assert.assertEquals("Check create ", uhe.getTimestamp(), result.getTimestamp());

        uhe = new UserHistoryEntry(result);
        uhe.setUser(new User(uhe.getUser()));
        uhe.setOvertime(7979);

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).put(Entity.json(uhe));

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        historyEntrys = (List<UserHistoryEntry>) response.readEntity((GenericType) new GenericType<List<UserHistoryEntry>>() {
        });

        for (UserHistoryEntry historyEntry : historyEntrys) {
            if (Objects.equals(historyEntry.getOvertime(), uhe.getOvertime())) {
                result = historyEntry;
            }
        }

        Assert.assertNotEquals("Check if edit doesn't works", uhe, result);

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build("PATCH", Entity.json(uhe)).invoke();

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        historyEntrys = (List<UserHistoryEntry>) response.readEntity((GenericType) new GenericType<List<UserHistoryEntry>>() {
        });

        Assert.assertFalse("Check if the remove worked via get", historyEntrys.contains(uhe));
    }

}
