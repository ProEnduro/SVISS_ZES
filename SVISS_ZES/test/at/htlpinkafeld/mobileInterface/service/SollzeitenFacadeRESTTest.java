/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.dao.jdbc.ConnectionManager;
import at.htlpinkafeld.mobileInterface.authorization.Credentials;
import at.htlpinkafeld.pojo.SollZeit;
import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class SollzeitenFacadeRESTTest {

    private WebTarget webTarget;
    private Client client;
    private String token;
    private static final String BASE_URI = "http://localhost:8084/SVISS_ZES/webresources";

    public SollzeitenFacadeRESTTest() {
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

        webTarget = client.target(BASE_URI).path("sollzeiten");
    }

    @After
    public void tearDown() {
        client.close();
        ConnectionManager.rollback();
    }

    /**
     * Test of findAll method, of class AbsenceFacadeREST.
     */
    @Test
    public void testFindAll() {
        System.out.println("findAll");
        Response result;
        List<SollZeit> szs;

        result = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        szs = (List<SollZeit>) result.readEntity(new GenericType<List<SollZeit>>() {
        });

        Assert.assertFalse("Check if getList is working", szs.isEmpty());
    }

    /**
     * Test of various methods, of class AbsenceFacadeREST.
     */
    @Test
    public void testCreateEditRemove() {
        System.out.println("create");
        SollZeit result;
        Response response;
        List<SollZeit> szs;
        Map<DayOfWeek, LocalTime> startTimeMap = new HashMap<>();
        startTimeMap.put(DayOfWeek.SUNDAY, LocalTime.of(8, 0));
        Map<DayOfWeek, LocalTime> endTimeMap = new HashMap<>();
        endTimeMap.put(DayOfWeek.SUNDAY, LocalTime.of(15, 0));

        SollZeit sz = new SollZeit(new User(BenutzerverwaltungService.getUserList().get(0)), startTimeMap, endTimeMap);
        Response res = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).post(Entity.json(sz));
        result = res.readEntity(SollZeit.class);
        Assert.assertEquals("Check if there is an Error in the create", sz.getUser(), result.getUser());

        sz = new SollZeit(result);
        sz.setUser(new User(sz.getUser()));
        startTimeMap.put(DayOfWeek.SUNDAY, LocalTime.of(7, 0));

        sz.setSollStartTimeMap(startTimeMap);

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).put(Entity.json(sz));

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        szs = (List<SollZeit>) response.readEntity((GenericType) new GenericType<List<SollZeit>>() {
        });

        for (SollZeit sollZeit : szs) {
            if (sollZeit.getUser().equals(sz.getUser()) && Objects.equals(sz.getSollZeitID(), sollZeit.getSollZeitID())) {
                result = sollZeit;
            }
        }

        Assert.assertEquals("Check if edit works", sz.getSollEndTime(DayOfWeek.SUNDAY), result.getSollEndTime(DayOfWeek.SUNDAY));

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build("PATCH", Entity.json(sz)).invoke();

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        szs = (List<SollZeit>) response.readEntity((GenericType) new GenericType<List<SollZeit>>() {
        });

        Assert.assertFalse("Check if the remove worked via get", szs.contains(sz));
    }
}
