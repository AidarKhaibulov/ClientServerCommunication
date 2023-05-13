package com.csc.clientservercommunication;

public interface AccessController {

     boolean isUserHaveAccessToTable(String userData,String table,String method);

    boolean canGrant(String authority,String userData);

    void grantAuthorityToUser(String username, String authority);
}
