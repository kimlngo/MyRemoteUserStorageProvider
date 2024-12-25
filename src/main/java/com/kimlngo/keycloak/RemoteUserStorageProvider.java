package com.kimlngo.keycloak;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

public class RemoteUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {
    private KeycloakSession keycloakSession;
    private ComponentModel componentModel;
    private UsersApiLegacyService usersService;

    public RemoteUserStorageProvider(KeycloakSession keycloakSession, ComponentModel componentModel,
                                     UsersApiLegacyService usersService) {
        this.keycloakSession = keycloakSession;
        this.componentModel = componentModel;
        this.usersService = usersService;
    }

    @Override
    public void close() {

    }

    @Override
    public UserModel getUserById(RealmModel realmModel, String id) {
        StorageId storageId = new StorageId(id);
        String email = storageId.getExternalId();

        return getUserByUsername(realmModel, email);
    }

    @Override
    public UserModel getUserByUsername(RealmModel realmModel, String username) {
        UserModel returnVal = null;

        User user = usersService.getUserByUserName(username);

        if (user != null) {
            returnVal = new UserAdapter(keycloakSession, realmModel, componentModel, user);
        }
        return returnVal;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realmModel, String email) {
        return getUserByUsername(realmModel, email);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        VerifyPasswordResponse verifyPasswordResponse = usersService.verifyUserPassword(userModel.getUsername(), credentialInput.getChallengeResponse());

        if(verifyPasswordResponse == null) {
            return false;
        }
        return verifyPasswordResponse.getResult();
    }
}
