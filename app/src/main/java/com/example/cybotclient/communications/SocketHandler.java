package com.example.cybotclient.communications;

import static com.example.cybotclient.util.Constants.B_NEWLINE;
import static com.example.cybotclient.util.Constants.CONNECTED_COLOR;
import static com.example.cybotclient.util.Constants.DISCONNECTED_COLOR;

import android.app.Activity;
import android.widget.RadioButton;

import com.example.cybotclient.R;
import com.example.cybotclient.activities.MainActivity;
import com.example.cybotclient.data.DataHandler;
import com.example.cybotclient.data.field.Field;
import com.example.cybotclient.fragments.BotFragment;
import com.example.cybotclient.fragments.FieldFragment;

import java.io.IOException;
import java.net.Socket;

public class SocketHandler implements Runnable {
    private final MainActivity activity;
    private final MainActivity.PagerAdapter adapter;
    private final String ip;
    private final int port;

    private InThread in;
    private OutThread out;

    public SocketHandler(MainActivity activity, MainActivity.PagerAdapter adapter, String ip, int port) {
        this.activity = activity;
        this.adapter = adapter;
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

    public void emergencyStop() {
        out.emergencyStop();
    }

    public boolean sendByte(byte b) {
        if (isConnected()) {
            out.sendByte(b);
            return true;
        }
        return false;
    }

    public boolean sendBytes(byte[] bytes) {
        for (byte b : bytes) {
            if (!sendByte(b)) return false;
        }
        return true;
    }

    public boolean sendCommand(String command) {
        if (!sendByte((byte) ':')) return false;
        if (!sendBytes(command.getBytes())) return false;
        return sendByte(B_NEWLINE);
    }

    public boolean outBusy() {
        return out.isBusy();
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ip, port);

            out = new OutThread(socket.getOutputStream());
            in = new InThread(activity, new DataHandler(activity, MainActivity.field, adapter), socket.getInputStream(), out);
            out.start();
            in.start();

            activity.runOnUiThread(() -> {
                RadioButton connectionStatus = activity.findViewById(R.id.connection);
                connectionStatus.setButtonTintList(CONNECTED_COLOR);
                connectionStatus.setText(R.string.radio_connected);
            });

            while (isConnected()) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(1000);
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
            activity.runOnUiThread(() -> {
                RadioButton connectionStatus = activity.findViewById(R.id.connection);
                connectionStatus.setButtonTintList(DISCONNECTED_COLOR);
                connectionStatus.setText(R.string.error_msg);
            });
        }
    }
}
