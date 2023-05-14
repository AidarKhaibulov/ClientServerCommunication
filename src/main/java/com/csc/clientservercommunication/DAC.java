package com.csc.clientservercommunication;


public class DAC implements AccessController{
    @Override
    public boolean isUserHaveAccessToTable(String userData, String table, String method) {
        String[] tablesInfo= userData.split(",");
        String curTableAccessInfo = "";
        for(String tableInfo:tablesInfo){
            if(tableInfo.charAt(0)==table.charAt(5)){
                curTableAccessInfo=tableInfo;
                break;
            }
        }
        switch (method){
            case "select"->{
                if(curTableAccessInfo.charAt(1)=='1')
                    return true;
            }
            case "insert"->{
                if(curTableAccessInfo.charAt(2)=='1')
                    return true;
            }
            case "delete"->{
                if(curTableAccessInfo.charAt(3)=='1')
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean canGrant(String authority, String userData) {
        String[] curTablesInfo= userData.split(",");
        String curTableAccessInfo = "";
        for(String tableInfo:curTablesInfo){
            if(tableInfo.charAt(0)==authority.charAt(0)){
                curTableAccessInfo=tableInfo;
                break;
            }
        }
        // if current user has no access to assigning table
        if(curTableAccessInfo.equals(""))
            return false;

        // 10__ - new data
        // 1101 - old data
        // checking if cur user has access to the levels provided in new data
        for (int i = 1; i < authority.length(); i++) {
            if(authority.charAt(i)!='_' && curTableAccessInfo.charAt(i)!='1')
                return false;
        }
        return true;
    }

    @Override
    public void grantAuthorityToUser(String username, String authority) {
        DBHandler db = new DBHandler();
        db.setDAC(username,authority);
    }

}
