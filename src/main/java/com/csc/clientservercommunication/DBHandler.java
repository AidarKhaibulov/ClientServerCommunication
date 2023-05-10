package com.csc.clientservercommunication;

import java.sql.*;

public class DBHandler {
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5433/postgres";
    static final String USER = "postgres";
    static final String PASS = "postgres";
    private Connection connect(){
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("postgre error");
        }

        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    public String selectData(String table){
        Connection conn= connect();
        PreparedStatement stmt ;
        try {
            stmt = conn.prepareStatement("SELECT * FROM " +table);
            ResultSet rs = stmt.executeQuery();
            StringBuilder result=new StringBuilder();
            while (rs.next()) {
                result.append(rs.getInt("id")+
                        rs.getString("name")+ rs.getString("value")+"*");
            }
            conn.close();
            return result.toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean authorizeUser(String credentials){
       //TODO: return checking user authorization
        return false;
    }
}
