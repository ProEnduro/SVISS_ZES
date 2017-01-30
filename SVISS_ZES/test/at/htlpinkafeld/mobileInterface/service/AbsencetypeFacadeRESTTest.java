/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.dao.jdbc.ConnectionManager;
import at.htlpinkafeld.mobileInterface.authorization.Credentials;
import at.htlpinkafeld.pojo.AbsenceType;
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
public class AbsencetypeFacadeRESTTest {

    private WebTarget webTarget;
    private Client client;
    private String token;
    private static final String BASE_URI = "http://localhost:8084/SVISS_ZES/webresources";

    public AbsencetypeFacadeRESTTest() {
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

        webTarget = client.target(BASE_URI).path("absencetype");
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
        List<AbsenceType> l;

        result = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        l = (List<AbsenceType>) result.readEntity(new GenericType<List<AbsenceType>>() {
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
        AbsenceType result;
        Response response;
        List<AbsenceType> absenceTypeL;
        AbsenceType at = new AbsenceType("NoPlan");
        Response res = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).post(Entity.json(at));
        result = res.readEntity(AbsenceType.class);

        Assert.assertNotEquals("Assert if create and key-generation worked", at.getAbsenceTypeID(), result.getAbsenceTypeID());

        at = new AbsenceType(result);
        at.setAbsenceName("%&?$§!NoPlan%&?$§!%&?$§!");

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).put(Entity.json(at));

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        absenceTypeL = (List<AbsenceType>) response.readEntity((GenericType) new GenericType<List<AbsenceType>>() {
        });

        for (AbsenceType absenceType : absenceTypeL) {
            if (absenceType.getAbsenceTypeID().equals(at.getAbsenceTypeID())) {
                result = absenceType;
            }
        }

        Assert.assertEquals("Assert if edit worked", at, result);

        webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build("PATCH", Entity.json(at)).invoke();

        response = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get();
        absenceTypeL = (List<AbsenceType>) response.readEntity((GenericType) new GenericType<List<AbsenceType>>() {
        });

        Assert.assertFalse("Assert if delete worked", absenceTypeL.contains(at));
    }

}
