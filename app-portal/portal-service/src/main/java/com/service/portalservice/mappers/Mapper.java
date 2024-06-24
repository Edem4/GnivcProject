package com.service.portalservice.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.service.portalservice.dto.RegistratorDTO;
import com.service.portalservice.dto.UserDTO;
import com.service.portalservice.models.Company;
import com.service.portalservice.models.User;
import com.service.portalservice.models.UserDataBase;
import com.service.portalservice.service.PasswordService;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.util.Collections;
import java.util.List;


public class Mapper {
    @Autowired
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private static final CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class,String.class);

    public static User getUserFromHeaders(HttpHeaders headers) throws JsonProcessingException {
        User user = new User();
        user.setUserId(headers.get("userId").get(0));
        user.setUsername(headers.get("userName").get(0));
        user.setEmail(headers.get("userEmail").get(0));
        user.setRoles(objectMapper.readValue(headers.get("roles").get(0), collectionType));
        return user;
    }


    public static UserRepresentation convertToUserRepresentation(RegistratorDTO registratorDTO){
        UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setEnabled(true);
        userRepresentation.setEmail(registratorDTO.getEmail());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setUsername(registratorDTO.getUsername());
        userRepresentation.setFirstName(registratorDTO.getFirstName());
        userRepresentation.setLastName(registratorDTO.getLastName());

        String password = PasswordService.generatePassword();
        userRepresentation.setCredentials(Collections.singletonList(createCredential(password)));

        return userRepresentation;
    }
    public static UserRepresentation convertToUserRepresentation(UserDTO userDTO){
        UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setEnabled(true);
        userRepresentation.setEmail(userDTO.getEmail());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setUsername(userDTO.getUsername());
        userRepresentation.setFirstName(userDTO.getFirstname());
        userRepresentation.setLastName(userDTO.getLastname());

        String password = PasswordService.generatePassword();
        userRepresentation.setCredentials(Collections.singletonList(createCredential(password)));

        return userRepresentation;
    }

    public static boolean checkRoleForCompany(UserDataBase user,List<String> roleCompany,String role) {
        if(role.equals("ADMIN")) return user.getRolesToCompany().contains(roleCompany.get(0));
        if(role.equals("LOGIST")) return user.getRolesToCompany().contains(roleCompany.get(1));
        if(role.equals("DRIVER")) return user.getRolesToCompany().contains(roleCompany.get(2));
        return false;
    }
    public static boolean checkRoleForCompany(UserDataBase user,List<String> roleCompany) throws JsonProcessingException {
        for(String roles: roleCompany){
            if(user.getRolesToCompany().contains(roles)) return true;
        }
        return false;
    }
    public static String parseRoleOfCompany(Company company, String role){
        if(role.equals("ADMIN")) return company.getRoleCompany().get(0);
        if(role.equals("LOGIST")) return company.getRoleCompany().get(1);
        if(role.equals("DRIVER")) return company.getRoleCompany().get(2);
        return null;
    }


    private static CredentialRepresentation createCredential(String password){
        CredentialRepresentation credentials = new CredentialRepresentation();

        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(password);
        credentials.setTemporary(false);

        return credentials;
    }

}
