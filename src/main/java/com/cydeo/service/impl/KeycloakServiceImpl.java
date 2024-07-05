package com.cydeo.service.impl;

import com.cydeo.config.KeycloakProperties;
import com.cydeo.dto.UserDTO;
import com.cydeo.service.KeyCloakService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.Arrays.asList;
import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;
@Service
public class KeycloakServiceImpl implements KeyCloakService {

    private final KeycloakProperties keycloakProperties;

    public KeycloakServiceImpl(KeycloakProperties keycloakProperties) {

        this.keycloakProperties = keycloakProperties;
    }

    @Override
    public Response userCreate(UserDTO userDTO) {
        /*
    •	CredentialRepresentation credential: Creates an instance of CredentialRepresentation to define the user’s password.
	•	credential.setType(CredentialRepresentation.PASSWORD): Sets the type of credential to PASSWORD.
	•	credential.setTemporary(false): Indicates that the password is not temporary.
	•	credential.setValue(userDTO.getPassWord()): Sets the password value from UserDTO
         */

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(userDTO.getPassWord());
         // these are whatever we create in the postman as a body
        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(userDTO.getUserName());
        keycloakUser.setFirstName(userDTO.getFirstName());
        keycloakUser.setLastName(userDTO.getLastName());
        keycloakUser.setEmail(userDTO.getUserName());
        keycloakUser.setCredentials(asList(credential));
        keycloakUser.setEmailVerified(true);
        keycloakUser.setEnabled(true);


        Keycloak keycloak = getKeycloakInstance(); //Retrieves a Keycloak instance using the method defined later in the class.
//	keycloak.realm(keycloakProperties.getRealm()): Gets the RealmResource for the specified realm.
        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
     //   realmResource.users(): Gets the UsersResource which provides user management functionalities.
        UsersResource usersResource = realmResource.users();

        // Create Keycloak user
        Response result = usersResource.create(keycloakUser);
// Extracts the user ID from the creation response. This method needs to be defined to parse the user ID from the response’s location header.
        String userId = getCreatedId(result);
        //Finds the client representation by the client ID. Assuming the client ID is unique, get(0) retrieves the first (and presumably only) client found.
        ClientRepresentation appClient = realmResource.clients()
                .findByClientId(keycloakProperties.getClientId()).get(0);
                  // Gets the client resource by client ID.
        RoleRepresentation userClientRole = realmResource.clients().get(appClient.getId())
                //Retrieves the role representation based on the role description provided in UserDTO.
                .roles().get(userDTO.getRole().getDescription()).toRepresentation();
      //  Gets the user resource by user ID.
        realmResource.users().get(userId).roles().clientLevel(appClient.getId())
                //Adds the client-level role to the user.
                .add(List.of(userClientRole));

//Closes the Keycloak client instance.
        keycloak.close();
        return result;
    }

    @Override
    public void delete(String userName) {
        //Retrieves a Keycloak instance.
        Keycloak keycloak = getKeycloakInstance();
         //Same as in the userCreate method, it gets the realm and users resources.
        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
        UsersResource usersResource = realmResource.users();
        /*
    •	usersResource.search(userName): Searches for users by username.
	•	userRepresentations.get(0).getId(): Retrieves the ID of the first user found (assuming the username is unique).
	•	usersResource.delete(uid): Deletes the user by user ID.
         */

        List<UserRepresentation> userRepresentations = usersResource.search(userName);
        String uid = userRepresentations.get(0).getId();
        usersResource.delete(uid);

        keycloak.close();
    }

    private Keycloak getKeycloakInstance(){  //Creates a new Keycloak client instance using properties for server URL, realm, username, password, and client ID. This instance is used to interact with Keycloak.
        return Keycloak.getInstance(keycloakProperties.getAuthServerUrl(),
                keycloakProperties.getMasterRealm(), keycloakProperties.getMasterUser()
                , keycloakProperties.getMasterUserPswd(), keycloakProperties.getMasterClient());
    }
}
