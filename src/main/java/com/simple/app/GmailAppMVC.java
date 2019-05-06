package com.simple.app;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.simple.app.configuration.CredentialsManager;
import com.simple.app.controller.GmailWebAppController;
import com.simple.app.model.MessageService;
import com.simple.app.model.ThreadService;
import com.simple.app.view.GmailAppView;

public class GmailAppMVC {
    private static final String APPLICATION_NAME = "simple gmail app";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private NetHttpTransport netHttpTransport;
    private CredentialsManager credentialsManager;
    private Credential credential;
    private ThreadService threadService;
    private Gmail gmail;
    private GmailService service;
    private MessageService messageService;

    public static void main(String[] args) {
        GmailAppMVC app = new GmailAppMVC();
    }

    public GmailAppMVC() {
        initConfiguration();
        GmailAppView view = new GmailAppView();
        GmailWebAppController controller = new GmailWebAppController(view, messageService, threadService);
        view.setVisible(true);
    }

    private void initConfiguration() {
        try {
            netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
            credentialsManager = new CredentialsManager(netHttpTransport);
            credential = credentialsManager.getCredentials();
            gmail = new Gmail.Builder(netHttpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            service = new GmailService(gmail);
            threadService = new ThreadService(service);
            messageService = new MessageService(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
