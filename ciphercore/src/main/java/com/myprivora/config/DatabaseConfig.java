package com.myprivora.config;

import java.io.FileInputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public class DatabaseConfig {

    static {
        getFirebaseConfig();

    }

    private static void getFirebaseConfig() {

        try {

            FileInputStream serviceAccount = new FileInputStream("D:\\java\\superx\\openjfx-21.0.12_windows-x64_bin-sdk\\PRIVORA\\ciphercore\\src\\main\\resources\\java26.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

}
