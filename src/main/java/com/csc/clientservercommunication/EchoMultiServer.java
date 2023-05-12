package com.csc.clientservercommunication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.io.*;
public class EchoMultiServer {
    /*
        1 - DAC
        2 - MAC
        3 - RBAC
     */
    private static final int accessHandleType=1;
    private static final Logger LOG = LoggerFactory.getLogger(EchoMultiServer.class);

    private ServerSocket serverSocket;

    public void start(int port) {
        System.out.println("Server started");
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                new EchoClientHandler(serverSocket.accept()).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }

    }

    public void stop() {
        try {

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                String[] userDataTypes=authorizeUser();

                String userData = "";
                switch (accessHandleType){
                    case 1->userData=userDataTypes[1];
                    case 2-> userData=userDataTypes[2];
                    case 3->userData=userDataTypes[0];
                }
                handleUsersRequests(userData);


            } catch (IOException e) {
                LOG.debug(e.getMessage());
            }
        }

        private String[] authorizeUser() throws IOException {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String credentials;
            while ((credentials = in.readLine()) != null) {
                Authenticator authenticator=new Authenticator(credentials);
                if (authenticator.isCredentialsValid(credentials)) {
                    out.println("Authorization complete!");
                    break;
                }
                out.println("Invalid login or password");
            }
            DBHandler db=new DBHandler();
            assert credentials != null;
            return db.getUserData(credentials);
        }
        private void handleUsersRequests(String userData) throws IOException {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String request;
            boolean isSessionActive=true;
            while (isSessionActive && ((request = in.readLine()) != null)) {
                String [] requestParams=request.split(" ");
                switch (requestParams[0]) {
                    case "exit" -> {
                        out.println("Session completed");
                        isSessionActive = false;
                        LOG.info("User session terminated");
                    }
                    case "help" -> out.println("exit - stop current session*select <table_name> - fetch all data");
                    case "select","insert","delete"->{
                        String result =executeQueryToDB(requestParams,userData);
                        out.println(result);
                    }
                    default -> out.print("Unknown command");
                }

            }
            in.close();
            out.close();
            clientSocket.close();
        }
        private String executeQueryToDB(String[] requestParams,String userData) {
            AccessController accessController=null;
            switch (accessHandleType){
                case 1->accessController = new DAC();
                case 2->accessController = new MAC();
                case 3->accessController = new RBAC();
            }

            DBHandler db= new DBHandler();
            final String warnMessage="Access denied";
            final String okMessage="200 OK";
            final String forbiddenMessage="403 FORBIDDEN";
            switch (requestParams[0]){
                case "select"-> {
                    boolean userHasAccess= accessController.isUserHaveAccessToTable(userData,requestParams[1],"select");
                    if(userHasAccess){
                        LOG.info(okMessage);
                        return db.selectData(requestParams[1]);
                    }
                    else{
                        LOG.warn(forbiddenMessage);
                        return warnMessage;
                    }
                }
                case "insert" -> {
                    boolean userHasAccess= accessController.isUserHaveAccessToTable(userData,requestParams[1],"insert");
                    if(userHasAccess) {
                        LOG.info(okMessage);
                        return db.insertData(requestParams[1], requestParams[2], requestParams[3], requestParams[4]);
                    }
                    else {
                        LOG.warn(forbiddenMessage);
                        return warnMessage;
                    }
                }
                case "delete" -> {
                    boolean userHasAccess= accessController.isUserHaveAccessToTable(userData,requestParams[1],"delete");
                    if(userHasAccess) {
                        LOG.info(okMessage);
                        return db.deleteData(requestParams[1], requestParams[2]);
                    }
                    else{
                        LOG.warn(forbiddenMessage);
                        return warnMessage;
                    }
                }
                default -> {
                    return "error while executing user queries";
                }
            }

        }
    }

    public static void main(String[] args) {
        EchoMultiServer server = new EchoMultiServer();
        server.start(5555);
    }

}