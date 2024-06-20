package com.service.portalservice.service;


import com.service.portalservice.exceptions.KeycloakException;
import com.service.portalservice.exceptions.UserNotFoundException;
import com.service.portalservice.repository.UserRepository;
import jakarta.ws.rs.core.Response;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class KeycloakService {
    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final RealmResource realm;
    @Value("${keycloak.realm}")
    private String KEYCLOAK_REALM;
    private final String ROLE_REGISTER = "REGISTRATOR";
    private final String ROLE_ADMIN = "ADMIN";
    private final String ROLE_LOGIST = "LOGIST";
    private final String ROLE_DRIVER = "DRIVER";
    private final MailService mailService;
    private DaDataService daDataService;

    public void addUserRegistrator(UserRepresentation userRepresentation) throws KeycloakException, UserNotFoundException {
        Response result = realm.users().create(userRepresentation);
        setRole(result, ROLE_REGISTER);
        sendPassword(userRepresentation);
    }
    public void addUser(UserRepresentation userRepresentation) throws KeycloakException, UserNotFoundException {
        Response result = realm.users().create(userRepresentation);
        sendPassword(userRepresentation);
    }

    public List<RoleRepresentation> createRolesForCreatedCompany(String companyInn) {
        RoleRepresentation admin = new RoleRepresentation(
                ROLE_ADMIN + companyInn, "", false
        );
        RoleRepresentation logist = new RoleRepresentation(
                ROLE_LOGIST + companyInn, "", false
        );
        RoleRepresentation driver = new RoleRepresentation(
                ROLE_DRIVER + companyInn, "", false
        );
        List.of(admin, logist, driver).forEach(role -> realm.roles().create(role));
        return List.of(admin, logist, driver);
    }


    public UserResource getUserResource(String id) throws UserNotFoundException {
        try {
            return realm.users().get(id);
        } catch (Exception e) {
            throw new UserNotFoundException("Invalid id");
        }

    }

    public String getUserIdByUserName(String username) throws UserNotFoundException {
        try {
            UserRepresentation userRepresentation = realm.users().searchByUsername(username, true).get(0);
            return userRepresentation.getId();
        } catch (Exception e) {
            throw new UserNotFoundException("Invalid username");
        }
    }


    private void sendPassword(UserRepresentation userRepresentation) {
        CredentialRepresentation credential = userRepresentation.getCredentials().get(0);

        String password = credential.getValue();

        mailService.sendSimpleMessage(userRepresentation.getEmail(), "Ваш пароль в сервисе", password);
    }

    private String getCreatedId(Response response) throws KeycloakException {
        URI location = response.getLocation();

        if (!response.getStatusInfo().equals(Response.Status.CREATED)) {
            Response.StatusType statusInfo = response.getStatusInfo();
            throw new KeycloakException("Create method returned status " +
                    statusInfo.getReasonPhrase() + " (Code: " + statusInfo.getStatusCode() + "); expected status: Created (201)", response);
        }

        if (location == null) {
            return null;
        }

        String path = location.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public void setRole(String id, String role, String roleCompany) throws KeycloakException, UserNotFoundException {
        RoleResource roleResource = realm.roles().get(role);
        RoleResource roleCompanyResource = realm.roles().get(roleCompany);
        UserResource userResource = getUserResource(id);

        RoleRepresentation rolesRepresentation = roleResource.toRepresentation();
        RoleRepresentation rolesRepresentationCompany = roleCompanyResource.toRepresentation();

        userResource.roles().realmLevel().add(Collections.singletonList(rolesRepresentation));
        userResource.roles().realmLevel().add(Collections.singletonList(rolesRepresentationCompany));
    }

    private void setRole(Response response, String role) throws KeycloakException, UserNotFoundException {
        RoleResource roleResource = realm.roles().get(role);
        UserResource userResource = getUserResource(getCreatedId(response));

        RoleRepresentation rolesRepresentation = roleResource.toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(rolesRepresentation));
    }

}