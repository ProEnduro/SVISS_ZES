/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.authorization;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Martin Six
 */
public class AuthenticationEndpointTest {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://zes.sviss.at/SVISS_ZES/webresources";

    public AuthenticationEndpointTest() {
    }

    @Before
    public void setUp() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("authentication");

    }

    @After
    public void tearDown() {
        client.close();
    }

    /**
     * Test of authenticateUser method, of class AuthenticationEndpoint.
     */
    @Test
    public void testAuthenticateUser() {
        System.out.println("authenticateUser");
        Credentials credentials = new Credentials("apptest", "apptest");
        Response result;
        String resultS;

        result = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML).post(Entity.xml(credentials), Response.class);
        resultS = result.readEntity(String.class);
        Assert.assertFalse(resultS.isEmpty());
        System.out.println(resultS);

        result = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(credentials, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
        resultS = result.readEntity(String.class);
        Assert.assertFalse(resultS.isEmpty());
        System.out.println(resultS);
    }

}
