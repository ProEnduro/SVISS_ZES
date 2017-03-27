/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.service;

import at.htlpinkafeld.dao.jdbc.ConnectionManager;
import at.htlpinkafeld.mobileInterface.authorization.Credentials;
import at.htlpinkafeld.pojo.Absence;
import at.htlpinkafeld.pojo.Holiday;
import at.htlpinkafeld.pojo.WorkTime;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author masix
 */
public class EventFacadeRestTest {

    private WebTarget webTarget;
    private Client client;
    private String token;
    private static final String BASE_URI = "http://localhost:8084/SVISS_ZES/webresources";

    public EventFacadeRestTest() {
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

        webTarget = client.target(BASE_URI).path("events");
    }

    @After
    public void tearDown() {
        client.close();
        ConnectionManager.rollback();
    }

    /**
     * Test of getEventList method, of class EventFacadeRest.
     */
    @Test
    public void testGetEventList() throws Exception {
        List eventList = new ArrayList();
        Response response;
        String result;
        Map<String, String> resultMap;

        response = webTarget.queryParam("username", "admin").
                queryParam("startDateTime", LocalDateTime.of(1990, 1, 1, 0, 0).format(DateTimeFormatter.ISO_DATE_TIME)).
                queryParam("endDateTime", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)).
                request(javax.ws.rs.core.MediaType.APPLICATION_JSON).
                header(HttpHeaders.AUTHORIZATION, "Bearer " + token).
                build("PATCH").invoke();

        result = response.readEntity(String.class);

        Gson gson = new Gson();
        resultMap = gson.fromJson(result, new TypeToken<Map<String, String>>() {
        }.getType());

        List<WorkTime> workTime = gson.fromJson(resultMap.get("worktime"), new TypeToken<List<WorkTime>>() {
        }.getType());
        List<Absence> absences = gson.fromJson(resultMap.get("absence"), new TypeToken<List<Absence>>() {
        }.getType());
        List<Holiday> holidays = gson.fromJson(resultMap.get("holiday"), new TypeToken<List<Holiday>>() {
        }.getType());

    }

}
