package com.kimlngo.keycloak;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class RemoteUserStorageProviderFactory implements UserStorageProviderFactory<RemoteUserStorageProvider> {
    private final String PROVIDER_NAME = "my-remote-user-storage-provider";

    @Override
    public RemoteUserStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new RemoteUserStorageProvider(keycloakSession, componentModel, new UsersApiLegacyService(keycloakSession));
    }

    @Override
    public String getId() {
        return PROVIDER_NAME;
    }
}
