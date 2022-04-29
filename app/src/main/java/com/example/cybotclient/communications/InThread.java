package com.example.cybotclient.communications;

import static com.example.cybotclient.util.Constants.B_SCAN_RESET;
import static com.example.cybotclient.util.Constants.B_WAIT;

import com.example.cybotclient.activities.MainActivity;
import com.example.cybotclient.data.DataHandler;

import java.io.IOException;
import java.io.InputStream;

public class InThread extends Thread {
    private final InputStream is;
    private final OutThread out;

    private boolean connected;

    private final DataHandler handler;
    private final MainActivity main;

    public InThread(MainActivity main, DataHandler handler, InputStream is, OutThread out) {
        this.main = main;
        this.handler = handler;
        this.is = is;
        this.out = out;
        connected = true;
    }

    public void disconnect() {
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        handler.start();
        try {
            label:
            while (connected) {
                if (is.available() > 0) {
                    byte data = (byte) is.read();
//                    handler.log("data: " + Integer.toHexString(data));
                    handler.handle(data);
//                    switch (data) {
//                        case -1: {
//                            handler.log("Disconnected " + Integer.toHexString(data));
//                            connected = false;
//                            break label;
//                        }
//                        case B_SCAN_RESET: {
//                            main.clearFragment(0);
//                        }
//                        case B_WAIT: {
//                            out.stopWait();
//                            break;
//                        }
//                        default: {
//                            handler.handle(data);
//                            break;
//                        }
//                    }
                }
                //noinspection BusyWait
                Thread.sleep(10);
            }

            is.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        handler.interrupt();
        connected = false;
    }
}
