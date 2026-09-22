package com.myprivora.model;

public class PrintRequest {

    private String requestId;

    private String documentId;

    private String documentName;

    private String userId;

    private String xeroxId;

    private String xeroxName;

    private int printCopies;

    private int printedCount;

    private int expiryMinutes;

    private String requestedAt;

    private String expiresAt;

    private String status;

    /*
     * SHA-256 hash of the user's 4-digit privacy PIN.
     *
     * IMPORTANT:
     *
     * The actual PIN is never stored.
     */
    private String pinHash;

    /*
     * AES-256 document key encrypted using
     * the selected Xerox centre's RSA public key.
     *
     * IMPORTANT:
     *
     * The original AES key is never stored in Firestore.
     *
     * This value is Base64 encoded because Firestore
     * stores this field as a String.
     */
    private String encryptedAesKey;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // Required by Firebase
    // =========================================================

    public PrintRequest() {
    }


    // =========================================================
    // FULL CONSTRUCTOR
    // =========================================================

    public PrintRequest(
            String requestId,
            String documentId,
            String documentName,
            String userId,
            String xeroxId,
            String xeroxName,
            int printCopies,
            int printedCount,
            int expiryMinutes,
            String requestedAt,
            String expiresAt,
            String status,
            String pinHash,
            String encryptedAesKey) {

        this.requestId = requestId;
        this.documentId = documentId;
        this.documentName = documentName;
        this.userId = userId;
        this.xeroxId = xeroxId;
        this.xeroxName = xeroxName;
        this.printCopies = printCopies;
        this.printedCount = printedCount;
        this.expiryMinutes = expiryMinutes;
        this.requestedAt = requestedAt;
        this.expiresAt = expiresAt;
        this.status = status;
        this.pinHash = pinHash;
        this.encryptedAesKey = encryptedAesKey;
    }


    // =========================================================
    // REQUEST ID
    // =========================================================

    public String getRequestId() {

        return requestId;
    }


    public void setRequestId(String requestId) {

        this.requestId = requestId;
    }


    // =========================================================
    // DOCUMENT ID
    // =========================================================

    public String getDocumentId() {

        return documentId;
    }


    public void setDocumentId(String documentId) {

        this.documentId = documentId;
    }


    // =========================================================
    // DOCUMENT NAME
    // =========================================================

    public String getDocumentName() {

        return documentName;
    }


    public void setDocumentName(String documentName) {

        this.documentName = documentName;
    }


    // =========================================================
    // USER ID
    // =========================================================

    public String getUserId() {

        return userId;
    }


    public void setUserId(String userId) {

        this.userId = userId;
    }


    // =========================================================
    // XEROX ID
    // =========================================================

    public String getXeroxId() {

        return xeroxId;
    }


    public void setXeroxId(String xeroxId) {

        this.xeroxId = xeroxId;
    }


    // =========================================================
    // XEROX NAME
    // =========================================================

    public String getXeroxName() {

        return xeroxName;
    }


    public void setXeroxName(String xeroxName) {

        this.xeroxName = xeroxName;
    }


    // =========================================================
    // PRINT COPIES
    // =========================================================

    public int getPrintCopies() {

        return printCopies;
    }


    public void setPrintCopies(int printCopies) {

        this.printCopies = printCopies;
    }


    // =========================================================
    // PRINTED COUNT
    // =========================================================

    public int getPrintedCount() {

        return printedCount;
    }


    public void setPrintedCount(int printedCount) {

        this.printedCount = printedCount;
    }


    // =========================================================
    // EXPIRY MINUTES
    // =========================================================

    public int getExpiryMinutes() {

        return expiryMinutes;
    }


    public void setExpiryMinutes(int expiryMinutes) {

        this.expiryMinutes = expiryMinutes;
    }


    // =========================================================
    // REQUESTED AT
    // =========================================================

    public String getRequestedAt() {

        return requestedAt;
    }


    public void setRequestedAt(String requestedAt) {

        this.requestedAt = requestedAt;
    }


    // =========================================================
    // EXPIRES AT
    // =========================================================

    public String getExpiresAt() {

        return expiresAt;
    }


    public void setExpiresAt(String expiresAt) {

        this.expiresAt = expiresAt;
    }


    // =========================================================
    // STATUS
    // =========================================================

    public String getStatus() {

        return status;
    }


    public void setStatus(String status) {

        this.status = status;
    }


    // =========================================================
    // PIN HASH
    // =========================================================

    public String getPinHash() {

        return pinHash;
    }


    public void setPinHash(String pinHash) {

        this.pinHash = pinHash;
    }


    // =========================================================
    // ENCRYPTED AES KEY
    // =========================================================

    public String getEncryptedAesKey() {

        return encryptedAesKey;
    }


    public void setEncryptedAesKey(
            String encryptedAesKey) {

        this.encryptedAesKey = encryptedAesKey;
    }
}