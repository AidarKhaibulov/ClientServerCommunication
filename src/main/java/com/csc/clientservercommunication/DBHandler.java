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
        PreparedStatement stmt ;
        try (Connection conn= connect()){
            stmt = conn.prepareStatement("SELECT * FROM " +table);
            ResultSet rs = stmt.executeQuery();
            StringBuilder result=new StringBuilder();
            while (rs.next()) {
                result.append(rs.getInt("id")+" "+
                        rs.getString("name")+" "+ rs.getString("value")+"*");
            }
            return result.toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String  insertData(String table,String id, String name, String value) {
        PreparedStatement stmt ;
        try (Connection conn= connect()){
            stmt = conn.prepareStatement("insert into "+table+" values ("+id+",'"+name+"',"+value+")");
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "Data inserted!";
    }

    public boolean authorizeUser(String credentials){
        String login=credentials.split(" ")[0];
        String password=credentials.split(" ")[1];

        PreparedStatement stmt ;
        try (Connection conn= connect()){
            stmt = conn.prepareStatement("select * from users where login='"+login +"' and password='"+ password+"'");
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
                return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public String[] getUserData(String credentials){
        String login=credentials.split(" ")[0];
        String password=credentials.split(" ")[1];

        PreparedStatement stmt ;
        try (Connection conn= connect()){
            stmt = conn.prepareStatement("select * from users where login='"+login +"' and password='"+ password+"'");
            ResultSet rs = stmt.executeQuery();
            rs.next();
            String[] res = new String[3];
            res[0]=rs.getString("role");
            res[1]=rs.getString("dac");
            res[2]=rs.getString("mac");
            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String deleteData(String table, String id) {
        PreparedStatement stmt ;
        try (Connection conn= connect()){
            stmt = conn.prepareStatement("delete from "+table+" where id='"+id+"'");
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "Data deleted!";
    }

    public void setMAC(String username, String authority) {
        String query="UPDATE users SET mac = '"+ authority +"' WHERE login = '"+ username +"'";
        setUserFiled(query);
    }

    public void setRBAC(String username, String authority) {
        String query="UPDATE users SET role = '"+ authority +"' WHERE login = '"+ username +"'";
        setUserFiled(query);
    }

    private void setUserFiled(String query) {
        PreparedStatement stmt ;
        try (Connection conn= connect()){
            stmt = conn.prepareStatement(query);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
