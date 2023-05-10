package com.csc.clientservercommunication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GreetClient {

    private static final Logger LOG = LoggerFactory.getLogger(EchoMultiServer.class);

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            LOG.debug(e.getMessage());
        }

    }

    public String sendMessage(String msg) {
        try {
            out.println(msg);
            return in.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            LOG.debug(e.getMessage());
        }
    }
    public static void main(String[] args) {
        EchoClient client1 = new EchoClient();
        client1.startConnection("127.0.0.1", 5555);
        Scanner sc= new Scanner(System.in);


        login(client1,sc);

        executeRequest(client1,sc);

    }

    private static void login(EchoClient client,Scanner sc) {
        String response;
        do {
            System.out.print("Enter login and password with whitespace");
            String str = sc.nextLine();
            response = client.sendMessage(str);
            System.out.println(response);
        }
        while(!response.equals("Authorization complete!"));
    }
    private static void executeRequest(EchoClient client,Scanner sc) {
        System.out.print("Type help to show all commands");
        String response;
        do {
            String request = sc.nextLine();
            response = client.sendMessage(request);
            System.out.println(response.replace("*","\n"));
        }
        while(!response.equals("Session completed"));
    }
}