package com.csc.clientservercommunication;

import java.util.Arrays;

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

}
