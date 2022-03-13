package com.example.cybotclient;

import static com.example.cybotclient.Constants.B_END_COMMS;
import static com.example.cybotclient.Constants.B_END_MESSAGE;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final ColorStateList disconnectedColor = new ColorStateList(
            new int[][] {
                    new int[] {-android.R.attr.state_enabled},
                    new int[] {android.R.attr.state_enabled}
            },
            new int[]{
                    Color.BLACK,
                    Color.rgb(147, 29, 29)
            }
    );
    private final ColorStateList connectingColor = new ColorStateList(
            new int[][] {
                    new int[] {-android.R.attr.state_enabled},
                    new int[] {android.R.attr.state_enabled}
            },
            new int[]{
                    Color.BLACK,
                    Color.rgb(237, 190, 20)
            }
    );
    private final ColorStateList connectedColor = new ColorStateList(
            new int[][] {
                    new int[] {-android.R.attr.state_enabled},
                    new int[] {android.R.attr.state_enabled}
            },
            new int[]{
                    Color.BLACK,
                    Color.rgb(19, 166, 40)
            }
    );

    private final Activity activity;
    private final String ip;
    private final int port;
    private Socket client;
    private boolean connected;

    private final DataHandler handler;

    private PrintWriter out;
    private BufferedReader in;

    public ConnectionHandler(Activity activity, String ip, int port) {
        this.activity = activity;
        this.ip = ip;
        this.port = port;
        connected = false;

        handler = new DataHandler();
    }

    public void disconnect() throws IOException {
        if (connected) {
            activity.runOnUiThread(() -> {
                RadioButton connectionStatus = activity.findViewById(R.id.connection);
                connectionStatus.setButtonTintList(disconnectedColor);
                connectionStatus.setText(R.string.disconnected);
            });
            client.close();
            in.close();
            out.close();
        }
    }

    @Override
    public void run() {
        try {
            activity.runOnUiThread(() -> {
                RadioButton connectionStatus = activity.findViewById(R.id.connection);
                connectionStatus.setButtonTintList(connectingColor);
                connectionStatus.setText(R.string.radio_connecting);
            });
            client = new Socket(ip, port);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            connected = true;
            activity.runOnUiThread(() -> {
                RadioButton connectionStatus = activity.findViewById(R.id.connection);
                connectionStatus.setButtonTintList(connectedColor);
                connectionStatus.setText(R.string.radio_connected);
            });

            byte read;
            while ((read = readByte()) != (byte) -1) {
                byte finalRead = read;
                activity.runOnUiThread(() -> {
                    handler.handle(finalRead);
                    if (handler.ready()) {
                        TextView logData = new TextView(activity);
                        logData.setText(handler.getPreppedMessage());
                        logData.setTextAppearance(R.style.logFont);

                        LinearLayout logLayout = activity.findViewById(R.id.log);
                        logLayout.addView(logData);
                        ScrollView logScrollView = activity.findViewById(R.id.logScrollView);
                        logScrollView.post(() -> logScrollView.fullScroll(View.FOCUS_DOWN));
                    }
                });
            }

            in.close();
            out.close();
            client.close();
            activity.runOnUiThread(() -> {
                RadioButton connectionStatus = activity.findViewById(R.id.connection);
                connectionStatus.setButtonTintList(disconnectedColor);
                connectionStatus.setText(R.string.disconnected);
            });
            connected = false;
        } catch (IOException e) {
            e.printStackTrace();
            activity.runOnUiThread(() -> {
                RadioButton connectionStatus = activity.findViewById(R.id.connection);
                connectionStatus.setButtonTintList(disconnectedColor);
                connectionStatus.setText(R.string.disconnected);
            });
            connected = false;
        }
    }

    public byte readByte() throws IOException {
        if (connected) {
            return (byte) in.read();
        }
        return 0;
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

    public boolean isConnected() {
        return connected;
    }
}
