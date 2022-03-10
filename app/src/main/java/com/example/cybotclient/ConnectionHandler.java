package com.example.cybotclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ConnectionHandler {
    private boolean connected;
    private Socket clientSocket;
    private PrintWriter out;
    private ClientReceive in;

    private final String ip;
    private final int port;

    public ConnectionHandler(String ip, int port) {
        this.ip = ip;
        this.port = port;
        connected = false;
    }

    public void setListener(ConnectionListener listener) {
        in.setListener(listener);
    }

    public boolean connect() {
        try {
            clientSocket = new Socket(ip, port);
            clientSocket.setKeepAlive(true);

            out = new PrintWriter(clientSocket.getOutputStream());

            in = new ClientReceive(clientSocket);
            in.start();
        } catch (Exception e) {
            connected = false;
            return false;
        }
        connected = true;
        return true;
    }

    public boolean disconnect() {
        connected = false;
        try {
            in.close();
            out.close();

            clientSocket.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void sendByte(byte data) {
        if (connected) {
            out.print(data);
        }
    }

    public void sendBytes(byte[] data) {
        for (byte b : data) {
            sendByte(b);
        }
    }

    public byte getByte() {
        if (connected) {
            return in.getByte();
        }
        return 0;
    }

    private static class ClientReceive extends Thread {
        private final Queue<Byte> dataBuffer;
        private final BufferedReader in;
        private ConnectionListener listener;

        public ClientReceive(Socket clientSocket) throws IOException {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            dataBuffer = new LinkedList<>();
            listener = null;
        }

        public void setListener(ConnectionListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            super.run();
            byte data;
            try {
                while ((data = (byte) in.read()) != 0) {
                    if (listener == null) {
                        dataBuffer.add(data);
                    } else {
                        listener.dataReceived(data);
                    }
                }
            } catch (Exception e) {
                interrupt();
            }
        }

        public byte getByte() {
            return dataBuffer.remove();
        }

        public void close() throws IOException {
            interrupt();
            in.close();
        }
    }

    public interface ConnectionListener {
        void dataReceived(byte data);
    }
}
