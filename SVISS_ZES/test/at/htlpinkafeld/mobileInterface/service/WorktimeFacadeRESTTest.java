/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.dao.jdbc.ConnectionManager;
import at.htlpinkafeld.mobileInterface.authorization.Credentials;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.pojo.WorkTime;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import java.time.LocalDateTime;
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
public class WorktimeFacadeRESTTest {

    private WebTarget webTarget;
    private Client client;
    private String token;
    private static final String BASE_URI = "http://localhost:8084/SVISS_ZES/webresources";

    public WorktimeFacadeRESTTest() {
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

        webTarget = client.target(BASE_URI).path("worktime");
    }

    @After
    public void tearDown() {
        client.close();
        ConnectionManager.rollback();
    }

    /**
     * Test of findAll method, of class WorktimeFacadeREST.
     */
    @Test
    public void testFindAll() {
        System.out.println("findAll");
        Response result;
        List<WorkTime> l;

        result = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        l = (List<WorkTime>) result.readEntity(new GenericType<List<WorkTime>>() {
        });

        Assert.assertFalse("Check for getList being empty", l.isEmpty());

        for (Object o : l) {
            o.toString();
        }
    }

    /**
     * Test of various methods, of class WorktimeFacadeREST.
     */
    @Test
    public void testCreateEditRemove() {
        System.out.println("create");
        WorkTime result;
        Response response;
        List<WorkTime> workTimes;
        WorkTime wt = new WorkTime(new User(BenutzerverwaltungService.getUserList().get(0)), LocalDateTime.of(1800, 7, 9, 0, 0), LocalDateTime.of(1800, 7, 12, 0, 0), 30, "startComment", "endComment");
        Response res = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).post(Entity.json(wt));
        result = res.readEntity(WorkTime.class);
        Assert.assertNotEquals("Check create and the auto-created key", wt.getTimeID(), result.getTimeID());

        wt = new WorkTime(result);
        wt.setUser(new User(wt.getUser()));
        wt.setStartComment("%&?$§!NoPlan%&?$§!%&?$§!");

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).put(Entity.json(wt));

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        workTimes = (List<WorkTime>) response.readEntity((GenericType) new GenericType<List<WorkTime>>() {
        });

        for (WorkTime workTime : workTimes) {
            if (workTime.getStartComment().equals(wt.getStartComment())) {
                result = workTime;
            }
        }

        Assert.assertEquals("Check if edit works", wt, result);

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build("PATCH", Entity.json(wt)).invoke();

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        workTimes = (List<WorkTime>) response.readEntity((GenericType) new GenericType<List<WorkTime>>() {
        });

        Assert.assertFalse("Check if the remove worked via get", workTimes.contains(wt));
    }

}
