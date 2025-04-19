package springKeycloak.service;

import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import springKeycloak.config.KeycloakConfig;
import springKeycloak.dto.UserDTO;
import java.util.Collections;
import java.util.List;

@Component
public class KeyCloakService {

    /**
     * This method is used to set user password credentials.
     * @param password
     * @return credentialRepresentation
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public static CredentialRepresentation createPasswordCredentials(String password){
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);

        return credentialRepresentation;
    }


    /**
     * This method is used to add user to keycloak.
     * @param user
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public static void addUserToKeycloak(UserDTO user){
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setEnabled(true);
        userRepresentation.getAccess();
        userRepresentation.getRequiredActions();
        userRepresentation.getCreatedTimestamp();
        userRepresentation.setOrigin("http://localhost:5000");
        userRepresentation.setCredentials(Collections.singletonList(createPasswordCredentials(user.getPassword())));

        UsersResource userResource = KeycloakConfig.getInstance().realm("TestRealm").users();
        userResource.create(userRepresentation);
    }

    /**
     * This method is used to fetch users from keycloak.
     * @return userRepresentation object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public static List<UserRepresentation> getKeycloakUsers(){
        return KeycloakConfig.getInstance().realm("TestRealm").users().list();
    }
}
