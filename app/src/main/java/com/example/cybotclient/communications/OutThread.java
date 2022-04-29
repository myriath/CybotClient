package com.example.cybotclient.communications;

import static com.example.cybotclient.util.Constants.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class OutThread extends Thread {
    private final OutputStream os;

    private boolean connected;
    private final LinkedBlockingQueue<Byte> outQueue;

    private boolean waitingForCmd;
    private boolean emergencyStop;

    public OutThread(OutputStream os) {
        this.os = os;
        connected = true;

        outQueue = new LinkedBlockingQueue<>(100);
        waitingForCmd = false;
        emergencyStop = false;
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

    public boolean isBusy() {
        return !outQueue.isEmpty() || waitingForCmd;
    }

    public void stopWait() {
        waitingForCmd = false;
    }

    public void emergencyStop() {
        emergencyStop = true;
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        try {
            while (connected) {
                try {
                    while ((outQueue.isEmpty() || waitingForCmd ) && !emergencyStop) {
                        Thread.sleep(100);
                    }
                    if (emergencyStop) {
                        emergencyStop = false;
                        os.write(B_EMERGENCY_STOP);
                        os.flush();
                    }

                    byte commandByte = outQueue.take();
                    if (commandByte == B_WAIT) {
                        waitingForCmd = true;
                    } else {
                        os.write(commandByte);
                        Thread.sleep(100);
                    }
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
