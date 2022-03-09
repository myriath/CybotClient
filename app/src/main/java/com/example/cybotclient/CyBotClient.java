package com.example.cybotclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CyBotClient {
    private Socket clientSocket;
    private PrintWriter toServer;
    private BufferedReader fromServer;

    private final String ip;
    private final int port;

    public CyBotClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void connect() throws IOException {
        clientSocket = new Socket(ip, port);

        toServer = new PrintWriter(clientSocket.getOutputStream(), true);
        fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws IOException {
        toServer.println(msg);
        return fromServer.readLine();
    }

    public byte sendByte(byte b) throws IOException {
        toServer.print(b);
        return (byte) fromServer.read();
    }

    public void disconnect() throws IOException {
        fromServer.close();
        toServer.close();

        clientSocket.close();
    }
}
