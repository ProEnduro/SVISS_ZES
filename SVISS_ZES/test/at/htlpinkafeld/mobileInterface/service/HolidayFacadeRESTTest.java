/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.dao.jdbc.ConnectionManager;
import at.htlpinkafeld.mobileInterface.authorization.Credentials;
import at.htlpinkafeld.pojo.Holiday;
import java.time.LocalDate;
import java.time.Month;
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
public class HolidayFacadeRESTTest {

    private WebTarget webTarget;
    private Client client;
    private String token;
    private static final String BASE_URI = "http://localhost:8084/SVISS_ZES/webresources";

    public HolidayFacadeRESTTest() {
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

        webTarget = client.target(BASE_URI).path("holiday");
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
        List<Holiday> l;

        result = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        l = (List<Holiday>) result.readEntity(new GenericType<List<Holiday>>() {
        });

        Assert.assertFalse(l.isEmpty());

        l.forEach((o) -> {
            o.toString();
        });
    }

    /**
     * Test of various methods, of class AbsenceFacadeREST.
     */
    @Test
    public void testCreateEditRemove() {
        System.out.println("create");
        Holiday result;
        Response response;
        List<Holiday> holidays;
        Holiday h = new Holiday(LocalDate.of(1980, Month.MARCH, 7), "NoPlan");
        Response res = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).post(Entity.json(h));
        result = res.readEntity(Holiday.class);
        Assert.assertEquals("Check if Holiday was inserted", h.getHolidayDate(), result.getHolidayDate());

        h = new Holiday(result);
        h.setHolidayComment("%&?$§!NoPlan%&?$§!%&?$§!");

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).put(Entity.json(h));

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        holidays = (List<Holiday>) response.readEntity((GenericType) new GenericType<List<Holiday>>() {
        });

        for (Holiday holiday : holidays) {
            if (h.getHolidayDate().equals(holiday.getHolidayDate())) {
                result = holiday;
            }
        }

        Assert.assertEquals("Check if the Update worked", h, result);

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build("PATCH", Entity.json(h)).invoke();

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        holidays = (List<Holiday>) response.readEntity((GenericType) new GenericType<List<Holiday>>() {
        });

        Assert.assertFalse("Check if the remove worked via get", holidays.contains(h));
    }

}
