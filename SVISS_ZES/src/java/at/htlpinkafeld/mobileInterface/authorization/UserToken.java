/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlpinkafeld.mobileInterface.authorization;

import at.htlpinkafeld.pojo.User;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author Martin Six
 */
public class UserToken implements Serializable {

    private final String token;
    private final User user;
    private LocalDateTime lastAuthentication;

    public UserToken(String token, User user, LocalDateTime lastAuthentication) {
        this.token = token;
        this.user = user;
        this.lastAuthentication = lastAuthentication;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getLastAuthentication() {
        return lastAuthentication;
    }

    public void resetLastAuthentication() {
        this.lastAuthentication = LocalDateTime.now();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.token);
        hash = 31 * hash + Objects.hashCode(this.user);
        hash = 31 * hash + Objects.hashCode(this.lastAuthentication);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserToken other = (UserToken) obj;
        if (!Objects.equals(this.token, other.token)) {
            return false;
        }
        return true;
    }
}
