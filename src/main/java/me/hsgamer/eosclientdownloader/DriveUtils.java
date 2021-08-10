package me.hsgamer.eosclientdownloader;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class DriveUtils {
    private static final String APPLICATION_NAME = "FPT-EOSClient Downloader";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_READONLY);

    private static Drive service;

    private DriveUtils() {
        // EMPTY
    }

    private static Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
        String clientId = MainConfig.CLIENT_ID.getValue();
        String clientSecret = MainConfig.CLIENT_SECRET.getValue();
        if (clientId.isEmpty() || clientSecret.isEmpty()) {
            throw new IllegalArgumentException("Client settings are not configured correctly");
        }
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientId, clientSecret, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private static Drive getService() throws IOException, GeneralSecurityException {
        if (service == null) {
            final NetHttpTransport netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
            service = new Drive.Builder(netHttpTransport, JSON_FACTORY, getCredentials(netHttpTransport))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
        return service;
    }

    protected static InputStream getFileAsInputStream(String driveId) throws IOException, GeneralSecurityException {
        return getService().files().get(driveId).setSupportsTeamDrives(true).executeMediaAsInputStream();
    }
}
