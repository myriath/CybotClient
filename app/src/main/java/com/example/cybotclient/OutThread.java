package com.example.cybotclient;

import static com.example.cybotclient.Constants.DISCONNECTED_COLOR;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.RadioButton;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

public class OutThread extends Thread {
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
    private final OutputStream os;

    private boolean connected;
    private final SynchronousQueue<Byte> outQueue;

    public OutThread(Activity activity, OutputStream os) {
        this.activity = activity;
        this.os = os;
        connected = true;

        outQueue = new SynchronousQueue<>();
    }

    public void sendByte(byte b) {
        try {
            outQueue.put(b);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        connected = false;
        interrupt();
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        try {
            while (connected) {
                try {
                    os.write(outQueue.take());
                } catch (InterruptedException e) {
                    break;
                }
                os.flush();
            }

            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        connected = false;
    }
}
