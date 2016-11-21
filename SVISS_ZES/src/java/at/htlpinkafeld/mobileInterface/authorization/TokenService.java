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

    private static final List<UserToken> registeredTokens = new LinkedList<>();

    public static synchronized void addToken(UserToken e) {
        registeredTokens.add(e);
    }

    public static synchronized void removeToken(UserToken e) {
        registeredTokens.remove(e);
    }

    public static synchronized boolean validateToken(String token) {
        boolean success = false;
        for (UserToken ut : registeredTokens) {
            if (ut.getToken().equals(token) && ut.getLastAuthentication().isBefore(ut.getLastAuthentication().plusMinutes(TOKENLIFETIME))) {
                success = true;
                ut.resetLastAuthentication();
            }
        }
        return success;
    }

    public static synchronized void cleanTokens() {
        List<UserToken> invalidUserTokens = new LinkedList<>();
        for (UserToken ut : registeredTokens) {
            if (ut.getLastAuthentication().isBefore(LocalDateTime.now().minusMinutes(TOKENLIFETIME))) {
                invalidUserTokens.add(ut);
            }
        }
        registeredTokens.removeAll(invalidUserTokens);
    }

}
