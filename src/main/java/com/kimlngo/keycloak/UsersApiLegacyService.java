package com.kimlngo.keycloak;

import jakarta.ws.rs.PathParam;
import org.jboss.logging.Logger;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;

public class UsersApiLegacyService {
    private static final Logger log = Logger.getLogger(UsersApiLegacyService.class);

    private static final String LEGACY_USER_WEB_SERVICE_ENDPOINT = "http://localhost:8099/users/";
    private KeycloakSession keycloakSession;

    public UsersApiLegacyService(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

    public User getUserByUserName(String username) {
        User user = null;

        try {
            user = SimpleHttp.doGet(LEGACY_USER_WEB_SERVICE_ENDPOINT + username, keycloakSession)
                             .asJson(User.class);
        } catch (Exception e) {
            log.error("Error fetching user " + username + " from external service: " + e.getMessage(), e);
        }
        return user;
    }

    public VerifyPasswordResponse verifyUserPassword(@PathParam("username") String username, String password) {
        SimpleHttp simpleHttp = SimpleHttp.doPost("http://localhost:8099/users/" + username + "/verify-password", keycloakSession);

        VerifyPasswordResponse verifyPasswordResponse = null;

        // Include password as form data in the request body
        simpleHttp.param("password", password);

        // Add headers if needed
        simpleHttp.header("Content-Type", "application/x-www-form-urlencoded");

        try {
            verifyPasswordResponse = simpleHttp.asJson(VerifyPasswordResponse.class);
        } catch (Exception e) {
            log.error("The provided password is incorrect", e);
        }

        return verifyPasswordResponse;
    }

}
