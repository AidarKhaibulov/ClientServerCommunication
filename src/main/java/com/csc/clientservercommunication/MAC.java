package com.csc.clientservercommunication;

public class MAC implements AccessController {
    private static final String[] ACCESS_LEVELS = {"NA", "confidential", "secret", "top_secret", "admin"};

        /*
         NA - has no access
         confidential - can manage all data in table1
         secret - also can read data from table2
         top_secret - can manage all data
         admin - can manage users access levels
        */

    @Override
    public boolean isUserHaveAccessToTable(String userData, String table, String method) {
        if (userData.equals(ACCESS_LEVELS[4]) || userData.equals(ACCESS_LEVELS[3])) return true;
        else if (userData.equals(ACCESS_LEVELS[2]) &&
                ((table.equals("table2") && method.equals("select")) || (table.equals("table1")))) return true;
        else if (userData.equals(ACCESS_LEVELS[1]) && table.equals("table1")) return true;
        else return false;
    }

    @Override
    public boolean canGrant(String authority, String userData) {
        return userData.equals(ACCESS_LEVELS[4]);
    }

    @Override
    public void grantAuthorityToUser(String username, String authority) {
        DBHandler db = new DBHandler();
        db.setMAC(username,authority);
    }
}
