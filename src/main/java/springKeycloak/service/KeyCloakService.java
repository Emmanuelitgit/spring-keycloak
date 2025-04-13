package springKeycloak.service;

import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import springKeycloak.config.KeycloakConfig;
import springKeycloak.models.User;

import java.util.Collections;

public class KeyCloakService {

    public static CredentialRepresentation createPasswordCredentials(String password){
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);

        return credentialRepresentation;
    }


    public static void addUserToKeycloak(User user){
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setCredentials(Collections.singletonList(createPasswordCredentials(user.getPassword())));

        UsersResource userResource = KeycloakConfig.getInstance().realm("Test Realm").users();
        userResource.create(userRepresentation);
    }
}
