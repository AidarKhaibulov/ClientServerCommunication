package com.csc.clientservercommunication;

public class MAC implements AccessController{
    @Override
    public boolean isUserHaveAccessToTable(String userData, String table, String method) {
        return false;
    }
}
