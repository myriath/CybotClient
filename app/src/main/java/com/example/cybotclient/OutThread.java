package com.example.cybotclient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.SynchronousQueue;

public class OutThread extends Thread {
    private final OutputStream os;

    private boolean connected;
    private final SynchronousQueue<Byte> outQueue;

    public OutThread(OutputStream os) {
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
