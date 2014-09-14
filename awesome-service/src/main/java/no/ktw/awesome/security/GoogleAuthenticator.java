/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ktw.awesome.security;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.sun.org.apache.xpath.internal.operations.Plus;
import java.io.InputStreamReader;
import java.util.Collections;

/**
 *
 * @author KellyATWhiteley
 */
public class GoogleAuthenticator {
    

//    /**
//     * Authorizes the installed application to access user's protected data.
//     */
//    private static Credential authorize() throws Exception {
//        
//        String APPLICATION_NAME = "PlusSample";
//        java.io.File DATA_STORE_DIR
//                = new java.io.File(System.getProperty("user.home"), ".store/plus_sample");
//        FileDataStoreFactory dataStoreFactory;
//
//        // Set up the HTTP transport and JSON factory
//        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//
//        // Load client secrets
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
//                new InputStreamReader(GoogleAuthenticator.class.getResourceAsStream("/client_secrets.json")));
//
//        // Set up authorization code flow
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                httpTransport, jsonFactory, clientSecrets,
//                Collections.singleton(PlusScopes.PLUS_ME)).setDataStoreFactory(dataStoreFactory)
//                .build();
//
//        // Authorize
//        Credential credential
//                = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
//
//        // Set up the main Google+ class
//        Plus plus = new Plus.Builder(httpTransport, jsonFactory, credential)
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//
//// Make a request to access your profile and display it to console
//        Person profile = plus.people().get("me").execute();
//        System.out.println("ID: " + profile.getId());
//        System.out.println("Name: " + profile.getDisplayName());
//        System.out.println("Image URL: " + profile.getImage().getUrl());
//        System.out.println("Profile URL: " + profile.getUrl());
//    }

}
