package com.myprivora.dao;

import java.util.ArrayList;
import java.util.List;

import com.myprivora.config.DatabaseConfig;
import com.myprivora.model.XeroxCentre;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

public class XeroxCentreDAO {

    private final Firestore db =
            DatabaseConfig.getFirestore();

    // =========================================================
    // CREATE XEROX CENTRE
    // =========================================================

    public boolean saveXeroxCentre(XeroxCentre centre) {

        try {

            db.collection("XeroxCentres")
                    .document(centre.getUid())
                    .create(centre)
                    .get();

            System.out.println(
                    "Xerox centre registered successfully."
            );

            return true;

        } catch (Exception e) {

            System.out.println(
                    "Error while registering Xerox centre."
            );

            e.printStackTrace();

            return false;
        }
    }

    // =========================================================
    // GET XEROX CENTRE BY UID
    // =========================================================

    public XeroxCentre getXeroxCentreById(String uid) {

        try {

            DocumentSnapshot document =
                    db.collection("XeroxCentres")
                            .document(uid)
                            .get()
                            .get();

            if (document.exists()) {

                return document.toObject(
                        XeroxCentre.class
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Error while finding Xerox centre."
            );

            e.printStackTrace();
        }

        return null;
    }

    // =========================================================
    // GET ALL XEROX CENTRES
    // =========================================================

    public List<XeroxCentre> getAllXeroxCentres() {

        List<XeroxCentre> centres =
                new ArrayList<>();

        try {

            ApiFuture<QuerySnapshot> future =
                    db.collection("XeroxCentres")
                            .get();

            QuerySnapshot snapshot =
                    future.get();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                XeroxCentre centre =
                        document.toObject(
                                XeroxCentre.class
                        );

                if (centre != null) {

                    centres.add(centre);
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Error while fetching Xerox centres."
            );

            e.printStackTrace();
        }

        return centres;
    }

    // =========================================================
    // GET ACTIVE XEROX CENTRES
    // =========================================================

    public List<XeroxCentre> getActiveXeroxCentres() {

        List<XeroxCentre> activeCentres =
                new ArrayList<>();

        List<XeroxCentre> allCentres =
                getAllXeroxCentres();

        for (XeroxCentre centre : allCentres) {

            if (centre.isAvailable()
                    && "ACTIVE".equalsIgnoreCase(
                            centre.getStatus())) {

                activeCentres.add(centre);
            }
        }

        return activeCentres;
    }
}