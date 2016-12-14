/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.dao.jdbc.ConnectionManager;
import at.htlpinkafeld.mobileInterface.authorization.Credentials;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.service.AbsenceService;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import java.time.LocalDateTime;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Martin Six
 */
public class AbsenceFacadeRESTTest {

    private WebTarget webTarget;
    private Client client;
    private String token;
    private static final String BASE_URI = "http://localhost:8084/SVISS_ZES/webresources";

    public AbsenceFacadeRESTTest() {
        ConnectionManager.setDebugInstance(true);
    }

    @Before
    public void setUp() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        WebTarget authTarget = client.target(BASE_URI).path("authentication");
        Credentials credentials = new Credentials("admin", "admin");
        token = authTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(credentials, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class).readEntity(String.class);

        webTarget = client.target(BASE_URI).path("absence");
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
        List<Absence> l;

        result = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        l = (List<Absence>) result.readEntity(new GenericType<List<Absence>>() {
        });

        Assert.assertFalse(l.isEmpty());

        for (Object o : l) {
            o.toString();
        }
    }

    /**
     * Test of various methods, of class AbsenceFacadeREST.
     */
    @Test
    public void testCreateEditRemove() {
        System.out.println("create");
        Absence result;
        Response response;
        List<Absence> absenceL;
        Absence a = new Absence(BenutzerverwaltungService.getUserList().get(0), AbsenceService.getList().get(0), LocalDateTime.of(1800, 7, 9, 0, 0), LocalDateTime.of(1800, 7, 12, 0, 0));

        result = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).post(Entity.json(a)).readEntity(Absence.class);
        Assert.assertNotEquals(result.getAbsenceID(), a.getAbsenceID());

        a = new Absence(result);
        a.setReason("%&?$§!NoPlan%&?$§!%&?$§!");

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).put(Entity.json(a));

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        absenceL = (List<Absence>) response.readEntity((GenericType) new GenericType<List<Absence>>() {
        });

        for (Absence absence : absenceL) {
            if (absence.getReason().equals(a.getReason())) {
                result = absence;
            }
        }

        Assert.assertEquals(result, a);

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).buildDelete().property("absence", a).invoke();

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        absenceL = (List<Absence>) response.readEntity((GenericType) new GenericType<List<Absence>>() {
        });

        Assert.assertFalse(absenceL.contains(a));
    }

}
