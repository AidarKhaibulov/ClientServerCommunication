package com.csc.clientservercommunication;

public class Authenticator {
    private String credentials;

    public Authenticator(String credentials) {
        this.credentials = credentials;
    }

    public boolean isCredentialsValid(String credentials){
        return true;
        /*DBHandler db= new DBHandler();
        return db.authorizeUser(credentials);*/
    }
}
