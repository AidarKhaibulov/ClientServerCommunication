package com.csc.clientservercommunication;

public class Authenticator {
    final String credentials;

    public Authenticator(String credentials) {
        this.credentials = credentials;
    }

    public boolean isCredentialsValid(String credentials){
        DBHandler db= new DBHandler();
        return db.authorizeUser(credentials);
    }
}
