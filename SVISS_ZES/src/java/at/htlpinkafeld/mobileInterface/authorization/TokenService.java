/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.authorization;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Six
 */
public class TokenService {

    private static final int TOKENLIFETIME = 5;

    private static final List<UserToken> REGISTERED_TOKENS = new LinkedList<>();

    public static synchronized void addToken(UserToken e) {
        REGISTERED_TOKENS.add(e);
    }

    public static synchronized void removeToken(UserToken e) {
        REGISTERED_TOKENS.remove(e);
    }

    public static synchronized boolean validateToken(String token) {
        boolean success = false;
        for (UserToken ut : REGISTERED_TOKENS) {
            if (ut.getToken().equals(token) && ut.getLastAuthentication().isBefore(ut.getLastAuthentication().plusMinutes(TOKENLIFETIME))) {
                success = true;
                ut.resetLastAuthentication();
            }
        }
        return success;
    }

    public static synchronized void cleanTokens() {
        List<UserToken> invalidUserTokens = new LinkedList<>();
        REGISTERED_TOKENS.stream().filter((ut) -> (ut.getLastAuthentication().isBefore(LocalDateTime.now().minusMinutes(TOKENLIFETIME)))).forEachOrdered((ut) -> {
            invalidUserTokens.add(ut);
        });
        REGISTERED_TOKENS.removeAll(invalidUserTokens);
    }

}
