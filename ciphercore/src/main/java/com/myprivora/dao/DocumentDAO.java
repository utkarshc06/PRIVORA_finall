package com.myprivora.dao;

import com.myprivora.config.DatabaseConfig;
import com.myprivora.model.Document;
import com.google.cloud.firestore.Firestore;

import java.util.ArrayList;
import java.util.List;

public class DocumentDAO {

    private final Firestore db;

    public DocumentDAO() {

        db =
                DatabaseConfig.getFirestore();
    }

    // =========================================================
    // SAVE DOCUMENT
    // =========================================================

    public boolean saveDocument(
            Document document) {

        try {

            if (document == null
                    || document.getDocumentId() == null
                    || document.getDocumentId().isBlank()) {

                return false;
            }

            db.collection("Documents")
                    .document(
                            document.getDocumentId()
                    )
                    .set(document)
                    .get();

            System.out.println(
                    "[DocumentDAO] "
                            + "Document saved successfully."
            );

            return true;

        } catch (Exception e) {

            System.out.println(
                    "[DocumentDAO] "
                            + "Error saving document: "
                            + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }

    // =========================================================
    // GET DOCUMENT BY ID
    // =========================================================

    public Document getDocumentById(
            String documentId) {

        try {

            if (documentId == null
                    || documentId.isBlank()) {

                return null;
            }

            Document document =
                    db.collection("Documents")
                            .document(documentId)
                            .get()
                            .get()
                            .toObject(
                                    Document.class
                            );

            if (document != null) {

                System.out.println(
                        "[DocumentDAO] "
                                + "Document found: "
                                + document.getFileName()
                );
            }

            return document;

        } catch (Exception e) {

            System.out.println(
                    "[DocumentDAO] "
                            + "Error fetching document: "
                            + e.getMessage()
            );

            e.printStackTrace();

            return null;
        }
    }

    // =========================================================
    // UPDATE STATUS
    // =========================================================

    public boolean updateStatus(
            String documentId,
            String status) {

        try {

            if (documentId == null
                    || documentId.isBlank()) {

                return false;
            }

            if (status == null
                    || status.isBlank()) {

                return false;
            }

            db.collection("Documents")
                    .document(documentId)
                    .update(
                            "status",
                            status
                    )
                    .get();

            System.out.println(
                    "[DocumentDAO] "
                            + "Status updated: "
                            + documentId
                            + " -> "
                            + status
            );

            return true;

        } catch (Exception e) {

            System.out.println(
                    "[DocumentDAO] "
                            + "Error updating status."
            );

            e.printStackTrace();

            return false;
        }
    }

    // =========================================================
    // GET DOCUMENTS BY OWNER
    // =========================================================

    public List<Document> getDocumentsByOwner(
            String ownerId) {

        List<Document> documents =
                new ArrayList<>();

        try {

            if (ownerId == null
                    || ownerId.isBlank()) {

                return documents;
            }

            db.collection("Documents")
                    .whereEqualTo(
                            "ownerId",
                            ownerId
                    )
                    .get()
                    .get()
                    .getDocuments()
                    .forEach(snapshot -> {

                        Document document =
                                snapshot.toObject(
                                        Document.class
                                );

                        if (document != null) {

                            documents.add(
                                    document
                            );
                        }
                    });

        } catch (Exception e) {

            System.out.println(
                    "[DocumentDAO] "
                            + "Error fetching owner documents."
            );

            e.printStackTrace();
        }

        return documents;
    }
}