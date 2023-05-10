package com.csc.clientservercommunication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.io.*;

public class EchoMultiServer {


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
                authorizeUser();

                handleUsersRequests();



            } catch (IOException e) {
                LOG.debug(e.getMessage());
            }
        }

        private void authorizeUser() throws IOException {
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


        }
        private void handleUsersRequests() throws IOException {
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
                        System.out.println("User session is terminated");
                    }
                    case "help" -> out.println("exit - stop current session");
                    case "select","insert","delete"->{
                        String result =executeQueryToDB(requestParams);
                        out.println(result);
                    }
                }

            }
            in.close();
            out.close();
            clientSocket.close();
        }

        private String executeQueryToDB(String[] requestParams) {
            DBHandler db= new DBHandler();
            return db.selectData(requestParams[1]);
        }
    }

    public static void main(String[] args) {
        EchoMultiServer server = new EchoMultiServer();
        server.start(5555);
    }

}