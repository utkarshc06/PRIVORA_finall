package com.myprivora.model;

public class Document {

    private String documentId;
    private String ownerId;
    private String fileName;
    private String filePath;
    private String uploadedAt;
    private String expiryTime;
    private String status;

    /*
     * Cloudinary deletion metadata.
     *
     * publicId identifies the uploaded Cloudinary object.
     * resourceType tells Cloudinary whether it is an image,
     * raw file, video, etc.
     */
    private String cloudinaryPublicId;
    private String cloudinaryResourceType;

    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public Document() {
    }

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Document(
            String documentId,
            String ownerId,
            String fileName,
            String filePath,
            String uploadedAt,
            String expiryTime,
            String status) {

        this.documentId = documentId;
        this.ownerId = ownerId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
        this.expiryTime = expiryTime;
        this.status = status;
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
    // OWNER ID
    // =========================================================

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    // =========================================================
    // FILE NAME
    // =========================================================

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    // =========================================================
    // FILE PATH
    // =========================================================

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // =========================================================
    // UPLOADED AT
    // =========================================================

    public String getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(String uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    // =========================================================
    // EXPIRY TIME
    // =========================================================

    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
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
    // CLOUDINARY PUBLIC ID
    // =========================================================

    public String getCloudinaryPublicId() {
        return cloudinaryPublicId;
    }

    public void setCloudinaryPublicId(
            String cloudinaryPublicId) {

        this.cloudinaryPublicId =
                cloudinaryPublicId;
    }

    // =========================================================
    // CLOUDINARY RESOURCE TYPE
    // =========================================================

    public String getCloudinaryResourceType() {
        return cloudinaryResourceType;
    }

    public void setCloudinaryResourceType(
            String cloudinaryResourceType) {

        this.cloudinaryResourceType =
                cloudinaryResourceType;
    }
}