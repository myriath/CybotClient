package com.example.cybotclient;

import static com.example.cybotclient.Constants.DISCONNECTED_COLOR;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class InThread extends Thread {
    private Activity activity;
    private InputStream is;

    private boolean connected;

    private DataHandler handler;

    public InThread(Activity activity, InputStream is) {
        this.activity = activity;
        this.is = is;
        connected = true;

        handler = new DataHandler();
    }

    public void disconnect() {
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        try {
            while (connected) {
                if (is.available() > 0) {
                    byte data = (byte) is.read();
                    activity.runOnUiThread(() -> {
                        handler.handle(data);
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
                Thread.sleep(10);
            }

            is.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        connected = false;
    }
}
