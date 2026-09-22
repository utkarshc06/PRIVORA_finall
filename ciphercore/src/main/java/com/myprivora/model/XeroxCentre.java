package com.myprivora.model;

public class XeroxCentre {

    private String uid;
    private String name;
    private String email;
    private String mobile;
    private String role;
    private String status;
    private boolean available;

    /*
     * Public RSA key of this Xerox centre.
     *
     * This key can safely be stored in Firestore.
     *
     * IMPORTANT:
     * The corresponding private key must NEVER
     * be stored in Firestore.
     */
    private String publicKey;


    // =========================================================
    // REQUIRED BY FIRESTORE
    // =========================================================

    public XeroxCentre() {
    }


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public XeroxCentre(
            String uid,
            String name,
            String email,
            String mobile,
            String role,
            String status,
            boolean available) {

        this.uid = uid;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.role = role;
        this.status = status;
        this.available = available;
    }


    // =========================================================
    // GET UID
    // =========================================================

    public String getUid() {
        return uid;
    }


    // =========================================================
    // SET UID
    // =========================================================

    public void setUid(String uid) {
        this.uid = uid;
    }


    // =========================================================
    // GET NAME
    // =========================================================

    public String getName() {
        return name;
    }


    // =========================================================
    // SET NAME
    // =========================================================

    public void setName(String name) {
        this.name = name;
    }


    // =========================================================
    // GET EMAIL
    // =========================================================

    public String getEmail() {
        return email;
    }


    // =========================================================
    // SET EMAIL
    // =========================================================

    public void setEmail(String email) {
        this.email = email;
    }


    // =========================================================
    // GET MOBILE
    // =========================================================

    public String getMobile() {
        return mobile;
    }


    // =========================================================
    // SET MOBILE
    // =========================================================

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    // =========================================================
    // GET ROLE
    // =========================================================

    public String getRole() {
        return role;
    }


    // =========================================================
    // SET ROLE
    // =========================================================

    public void setRole(String role) {
        this.role = role;
    }


    // =========================================================
    // GET STATUS
    // =========================================================

    public String getStatus() {
        return status;
    }


    // =========================================================
    // SET STATUS
    // =========================================================

    public void setStatus(String status) {
        this.status = status;
    }


    // =========================================================
    // GET AVAILABLE
    // =========================================================

    public boolean isAvailable() {
        return available;
    }


    // =========================================================
    // SET AVAILABLE
    // =========================================================

    public void setAvailable(boolean available) {
        this.available = available;
    }


    // =========================================================
    // GET PUBLIC KEY
    // =========================================================

    public String getPublicKey() {
        return publicKey;
    }


    // =========================================================
    // SET PUBLIC KEY
    // =========================================================

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}