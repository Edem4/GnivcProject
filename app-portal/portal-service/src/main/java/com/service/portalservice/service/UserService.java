package com.service.portalservice.service;

import com.service.portalservice.dto.*;
import com.service.portalservice.exceptions.*;
import com.service.portalservice.mappers.Mapper;
import com.service.portalservice.models.Company;
import com.service.portalservice.models.User;
import com.service.portalservice.models.UserDataBase;
import com.service.portalservice.repository.UserRepository;
import lombok.Data;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Data
public class UserService {
    private final KeycloakService keycloakService;
    private final RealmResource realm;
    @Value("${keycloak.realm}")
    private String KEYCLOAK_REALM;
//    private String ROLE_REGISTER = "REGISTERATOR";
    private final UserRepository userRepository;

    private MailService mailService;
//    private DaDataService daDataService;

    @Autowired
    public UserService(RealmResource realm, MailService mailService, KeycloakService keycloakService,  UserRepository userRepository) {
        this.realm = realm;
        this.mailService = mailService;
        this.keycloakService = keycloakService;
        this.userRepository = userRepository;
    }

    //СОЗДАНИЕ НОВОГО ПОЛЬЗОВАТЕЛЯ
    public void createNewUserRegister(RegistratorDTO registratorDTO) throws UserNotCreatedException, UserNotFoundException {
        UserRepresentation userRepresentation = Mapper.convertToUserRepresentation(registratorDTO);
        try {
            keycloakService.addUserRegistrator(userRepresentation);
            addUserDataBase(userRepresentation);
        } catch (KeycloakException e) {
            throw new UserNotCreatedException(e.getMessage(),
                    (HttpStatus) HttpStatusCode.valueOf(e.getResponse().getStatus()));
        }
    }
    public void createNewUser(UserDTO userDTO) throws UserNotCreatedException, UserNotFoundException {
        UserRepresentation userRepresentation = Mapper.convertToUserRepresentation(userDTO);
        try {
            keycloakService.addUser(userRepresentation);
            addUserDataBase(userRepresentation);
        } catch (KeycloakException e) {
            throw new UserNotCreatedException(e.getMessage(),
                    (HttpStatus) HttpStatusCode.valueOf(e.getResponse().getStatus()));
        }
    }

    //ДОБАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ В БД
    private void addUserDataBase(UserRepresentation userRepresentation) throws UserNotFoundException, UserNotCreatedException {
        UserDataBase userDataBase = new UserDataBase();
        userDataBase.setUserId(keycloakService.getUserIdByUserName(userRepresentation.getUsername()));
        userDataBase.setUsername(userRepresentation.getUsername());
        userDataBase.setFirstName(userRepresentation.getFirstName());
        userDataBase.setLastName(userRepresentation.getLastName());
        userDataBase.setUserEmail(userRepresentation.getEmail());
        userRepository.save(userDataBase);
    }

    //ДОБАВЛЕНИЕ РОЛИ КОМПАНИИ ПОЛЬЗОВАТЕЛЮ
    public void addRoleUser(User user, String role, String roleCompany) throws UserNotFoundException, KeycloakException {
        keycloakService.setRole(user.getUserId(), role, roleCompany);
        UserDataBase userDataBase = getUserFromDateBase(user.getUserId());
        userDataBase.setRolesToCompany(roleCompany);
        userRepository.save(userDataBase);
    }

    //ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЯ ИЗ БД
    public UserDataBase getUserFromDateBase(String userId) throws UserNotFoundException {
        Optional<UserDataBase> optionalUser = userRepository.findById(userId);
         if(optionalUser.isEmpty()) {
             throw new UserNotFoundException("Пользователь не найден");
         }
        return optionalUser.get();
    }
    public UserDataBase getUser(String userName) throws UserNotFoundException {
        return userRepository.findByUsername(userName);
    }


    //СМЕНА ПАРОЛЯ ПОЛЬЗОВАТЕЛЯ
    public void resetPassword(ResetPasswordDTO passwordDTO, User user) throws UserNotFoundException {
        UserResource userResource = keycloakService.getUserResource(user.getUserId());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(passwordDTO.getNewPassword());

        userResource.resetPassword(credential);
    }

    public void changeUserData(ChangeUserDataDTO changeUserDataDTO, User user) throws UserNotFoundException {
        UserResource userResource = keycloakService.getUserResource(user.getUserId());
        UserDataBase userDataBase = getUserFromDateBase(user.getUserId());
        UserRepresentation userRepresentation = userResource.toRepresentation();

        if(changeUserDataDTO.getFirstName() != null) {
            userRepresentation.setFirstName(changeUserDataDTO.getFirstName());
            userDataBase.setFirstName(changeUserDataDTO.getFirstName());
        }
        if(changeUserDataDTO.getLastName() != null) {
            userRepresentation.setLastName(changeUserDataDTO.getLastName());
            userDataBase.setLastName(changeUserDataDTO.getLastName());
        }
        if(changeUserDataDTO.getEmail() != null) {
            userRepresentation.setEmail(changeUserDataDTO.getEmail());
            userDataBase.setUserEmail(changeUserDataDTO.getEmail());
        }
        userResource.update(userRepresentation);
        userRepository.save(userDataBase);
    }


//    public List<UserDTO> getUsers(GetCompanyDTO companyDTO, User user) throws ForbiddenException, CompanyNotFoundException {
//        if(!user.getRoles().containsKey(companyDTO.getName())){
//            throw new ForbiddenException("You don't have the necessary authority");
//        }
//
//        if(!user.getClientRoles().get(companyDTO.getName()).equals("ADMIN")){
//            throw new ForbiddenException("You don't have the necessary authority");
//        }
//
//        try {
//            keycloakService.getClientIdByName(companyDTO.getName());
//        } catch (ClientNotFoundException e){
//            throw new CompanyNotFoundException("Company not found");
//        }
//
//
//        List<UserRepresentation> users = keycloakService.getAllUSer();
//
//        List<UserRepresentation> usersFromCompany = getUsersFromCompany(users, companyDTO.getName());
//
//        List<UserDTO> userDTOS = new ArrayList<>();
//
//        for(UserRepresentation userRepresentation : usersFromCompany){
//            userDTOS.add(Mapper.getUserDTOFromRepresentation(userRepresentation, companyDTO.getName()));
//        }
//
//        return userDTOS;
//    }

}