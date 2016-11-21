/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.authorization;

import at.htlpinkafeld.pojo.User;
import at.htlpinkafeld.service.BenutzerverwaltungService;
import java.time.LocalDateTime;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author Martin Six
 */
@Path("/authentication")
public class AuthenticationEndpoint {

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response authenticateUser(final Credentials credentials) {

        String username = credentials.getUsername();
        String password = credentials.getPassword();

        try {

            // Authenticate the user using the credentials provided
            authenticate(username, password);

            // Issue a token for the user
            String token = issueToken(username);

            // Return the token on the response
            return Response.ok(token).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    private void authenticate(String username, String password) throws Exception {
        User u = BenutzerverwaltungService.getUser(username);
        if ((u == null) || (!u.getPass().equals(password)) || (u.isDisabled() == true)) {
            throw new RuntimeException();
        }
    }

    private String issueToken(String username) {
        User u = BenutzerverwaltungService.getUser(username);
        String token = RandomStringUtils.randomAlphanumeric(20);
        TokenService.addToken(new UserToken(token, u, LocalDateTime.now()));
        return token;
    }
}
