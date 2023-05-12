package com.csc.clientservercommunication;

public class RBAC implements AccessController {
    /*
         Roles legend:
         R1 - user can only read data from table1
         C1 - user can both read and create data inside table1
         D1 - user have full access to table1

         R2 - user can only read data from table2
         C2 - user can both read and create data inside table2
         D2 - user have full access to table2

         ADMIN - can manage all data
         */


    @Override
    public boolean isUserHaveAccessToTable(String userData, String table, String method) {
        int tableAccessLevel = userData.charAt(1);
        int tableLevel = table.charAt(5);
        if (userData.equals("ADMIN")) return true;
        else if (tableAccessLevel != tableLevel)
            return false;
        else {
            char accessLevel = userData.charAt(0);
            return accessLevel == 'D'
                    || (accessLevel == 'C' && !method.equals("delete"))
                    || method.equals("select");
        }
    }
}
