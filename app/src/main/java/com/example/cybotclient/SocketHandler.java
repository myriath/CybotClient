package com.example.cybotclient;

import static com.example.cybotclient.Constants.CONNECTED_COLOR;
import static com.example.cybotclient.Constants.DISCONNECTED_COLOR;

import android.app.Activity;
import android.widget.RadioButton;

import java.io.IOException;
import java.net.Socket;

public class SocketHandler implements Runnable {
    private final Activity activity;
    private final String ip;
    private final int port;

    private InThread in;
    private OutThread out;

    public SocketHandler(Activity activity, String ip, int port) {
        this.activity = activity;
        this.ip = ip;
        this.port = port;
    }

    public void disconnect() {
        try {
            in.disconnect();
            out.disconnect();
            in.join();
            out.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        if (in != null && out != null) {
            if (in.isConnected() != out.isConnected()) {
                disconnect();
            }
            return in.isConnected() && out.isConnected();
        }
        return false;
    }

    public void sendByte(byte b) {
        out.sendByte(b);
    }

    public void sendBytes(byte[] bytes) {
        for (byte b : bytes) {
            sendByte(b);
        }
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ip, port);

            in = new InThread(activity, socket.getInputStream());
            out = new OutThread(activity, socket.getOutputStream());
            in.start();
            out.start();

            activity.runOnUiThread(() -> {
                RadioButton connectionStatus = activity.findViewById(R.id.connection);
                connectionStatus.setButtonTintList(CONNECTED_COLOR);
                connectionStatus.setText(R.string.radio_connected);
            });

            while (isConnected()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            socket.close();

            activity.runOnUiThread(() -> {
                RadioButton connectionStatus = activity.findViewById(R.id.connection);
                connectionStatus.setButtonTintList(DISCONNECTED_COLOR);
                connectionStatus.setText(R.string.disconnected);
            });
        } catch (IOException e) {

        }
    }
}
