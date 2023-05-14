package com.csc.clientservercommunication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    /*
        1 - DAC
        2 - MAC
        3 - RBAC
     */
    private static final int accessHandleType = 1;
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private ServerSocket serverSocket;

    public static void main(String[] args) {
        Server server = new Server();
        server.start(5555);
    }

    public void start(int port) {
        switch (accessHandleType) {
            case 1 -> LOG.info("Server started with DAC authorization strategy");
            case 2 -> LOG.info("Server started with MAC authorization strategy");
            case 3 -> LOG.info("Server started with RBAC authorization strategy");
        }
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

        private static AccessController applyCurrentAccessController() {
            AccessController accessController = null;
            switch (accessHandleType) {
                case 1 -> accessController = new DAC();
                case 2 -> accessController = new MAC();
                case 3 -> accessController = new RBAC();
            }
            return accessController;
        }

        public void run() {
            try {
                String[] userDataTypes = authorizeUser();

                String userData = "";
                switch (accessHandleType) {
                    case 1 -> userData = userDataTypes[1];
                    case 2 -> userData = userDataTypes[2];
                    case 3 -> userData = userDataTypes[0];
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
                Authenticator authenticator = new Authenticator(credentials);
                if (authenticator.isCredentialsValid(credentials)) {
                    out.println("Authorization complete!");
                    break;
                }
                out.println("Invalid login or password");
            }
            DBHandler db = new DBHandler();
            assert credentials != null;
            return db.getUserData(credentials);
        }

        private void handleUsersRequests(String userData) throws IOException {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String request;
            boolean isSessionActive = true;
            while (isSessionActive && ((request = in.readLine()) != null)) {
                String[] requestParams = request.split(" ");
                switch (requestParams[0]) {
                    case "exit" -> {
                        out.println("Session completed");
                        isSessionActive = false;
                        LOG.info("User session terminated");
                    }
                    case "help" ->
                            out.println("exit - stop current session*select <table_name> - fetch all data*insert <table_name> <id> <name> <value>- insert new data*delete <table_name> <id> - delete data*grant <username> <authority>- grant user authority");
                    case "select", "insert", "delete" -> {
                        String result = executeQueryToDB(requestParams, userData);
                        out.println(result);
                    }
                    case "grant" -> {
                        String result = grantAuthority(requestParams[1], requestParams[2], userData);
                        out.println(result);
                    }
                    default -> out.print("Unknown command");
                }

            }
            in.close();
            out.close();
            clientSocket.close();
        }

        private String grantAuthority(String username, String authority, String userData) {
            AccessController accessController = applyCurrentAccessController();

            boolean userCanGrant = accessController.canGrant(authority, userData);
            if (userCanGrant) {
                accessController.grantAuthorityToUser(username, authority);
                return "Granted!";
            } else return "You cannot do that";
        }

        private String executeQueryToDB(String[] requestParams, String userData) {
            AccessController accessController = applyCurrentAccessController();
            DBHandler db = new DBHandler();
            final String warnMessage = "Access denied";
            final String okMessage = String.format("%s %s - 200 OK ", requestParams[0], requestParams[1]);
            final String forbiddenMessage = String.format("%s %s - 403 FORBIDDEN", requestParams[0], requestParams[1]);

            switch (requestParams[0]) {
                case "select" -> {
                    boolean userHasAccess = accessController.isUserHaveAccessToTable(userData, requestParams[1], "select");
                    if (userHasAccess) {
                        LOG.info(okMessage);
                        return db.selectData(requestParams[1]);
                    } else {
                        LOG.warn(forbiddenMessage);
                        return warnMessage;
                    }
                }
                case "insert" -> {
                    boolean userHasAccess = accessController.isUserHaveAccessToTable(userData, requestParams[1], "insert");
                    if (userHasAccess) {
                        LOG.info(okMessage);
                        return db.insertData(requestParams[1], requestParams[2], requestParams[3], requestParams[4]);
                    } else {
                        LOG.warn(forbiddenMessage);
                        return warnMessage;
                    }
                }
                case "delete" -> {
                    boolean userHasAccess = accessController.isUserHaveAccessToTable(userData, requestParams[1], "delete");
                    if (userHasAccess) {
                        LOG.info(okMessage);
                        return db.deleteData(requestParams[1], requestParams[2]);
                    } else {
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

}