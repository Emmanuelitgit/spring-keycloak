package springKeycloak.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.stereotype.Component;

@Component
public class KeycloakConfig {

    static Keycloak keycloak = null;
    final static String serverUrl = "http://localhost:8080";
    public final static String realm = "TestRealm";
    final static String clientId = "test-app";
    final static String clientSecret = "xK7Ds7giCVwRnycwmGWVy90z6cYuyjKA";
    final static String userName = "eyidana";
    final static String password = "1234";


    public KeycloakConfig() {
    }

    public static Keycloak getInstance() {
        if (keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(userName)
                    .password(password)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .resteasyClient(new ResteasyClientBuilder()
                            .connectionPoolSize(10)
                            .build())
                    .build();
        }
        return keycloak;
    }

}
