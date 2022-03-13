package com.example.cybotclient;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class InThread extends Thread {
    private final Activity activity;
    private final InputStream is;

    private boolean connected;

    private final DataHandler handler;

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
                    if (data == -1) break;
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
                //noinspection BusyWait
                Thread.sleep(10);
            }

            is.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        connected = false;
    }
}
