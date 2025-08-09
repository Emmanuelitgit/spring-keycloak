package springKeycloak.service;

import jakarta.transaction.Transactional;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springKeycloak.config.KeycloakConfig;
import springKeycloak.dto.UserDTO;
import springKeycloak.models.setup.RoleSetUp;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Component
public class KeyCloakService {

    private final RoleSetUpService roleSetUpService;
    RealmResource realmInstance = KeycloakConfig.getInstance().realm("TestRealm");

    @Autowired
    public KeyCloakService(RoleSetUpService roleSetUpService) {
        this.roleSetUpService = roleSetUpService;
    }

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
    @Transactional
    public void addUserToKeycloak(UserDTO user){
        // getting role name by the role id from the role setup
        RoleSetUp role = roleSetUpService.getRoleById(user.getRole());

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);
        userRepresentation.setOrigin("http://localhost:5000");
        userRepresentation.setCredentials(Collections.singletonList(createPasswordCredentials(user.getPassword())));

        // saving user to keycloak
        UsersResource userResource = realmInstance.users();
        Response response = userResource.create(userRepresentation);

        // checking response status to proceed
        if (response.getStatus() == 201){

            // get role from keycloak by the role name
            RoleRepresentation realmRole = realmInstance
                    .roles()
                    .get(role.getName())
                    .toRepresentation();

            // assign role to user in keycloak
            UsersResource usersResource = realmInstance.users();
            List<UserRepresentation> users = realmInstance.users().search(user.getUsername(), 0, 1);
            String userId = users.get(0).getId();
            usersResource.get(userId)
                    .roles()
                    .realmLevel()
                    .add(Collections.singletonList(realmRole));
        }

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

    /**
     * This method is used to add role to keycloak. it is called in the roleSetup service class when adding a new role setup.
     * @param role
     * @auther Emmanuel Yidana
     * @createdAt 21st April 2025
     */
    public static void addRoleToKeycloak(String role){
        RoleRepresentation newRole = new RoleRepresentation();
        newRole.setName(role);
        KeycloakConfig.getInstance().realm("TestRealm").roles().create(newRole);
    }
}