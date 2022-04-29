package com.example.cybotclient.data;

import static com.example.cybotclient.util.Constants.*;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.cybotclient.R;
import com.example.cybotclient.activities.MainActivity;
import com.example.cybotclient.data.field.Field;
import com.example.cybotclient.data.field.FieldObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DataHandler extends Thread {
    private final MainActivity activity;
    private final Field field;
    private final MainActivity.PagerAdapter adapter;
    private final StringBuilder builder;
    private final ArrayBlockingQueue<Byte> bytes;

    private enum State {
        NONE, LOG, SCAN, OBJECT, MOVE, LOGEDGE
    }
    private State state = State.NONE;

    public DataHandler(MainActivity activity, Field field, MainActivity.PagerAdapter adapter) {
        this.activity = activity;
        this.field = field;
        this.adapter = adapter;
        builder = new StringBuilder();
        bytes = new ArrayBlockingQueue<>(100);
    }

    public void handle(byte data) {
        try {
            bytes.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                switch (state) {
                    case NONE: {
                        switch (bytes.take()) {
                            case (B_SCAN_RESET):
                                activity.clearFragment(0);
                                break;
                            case (B_LOG):
                                state = State.LOG;
                                break;
                            case (B_MOVE):
                                state = State.MOVE;
                                break;
                            case (B_SCAN_DATA):
                                state = State.SCAN;
                                break;
                            case (B_OBJ):
                                state = State.OBJECT;
                                break;
                            case (B_LOG_EDGE):
                                state = State.LOGEDGE;
                                break;
                        }
                        break;
                    }
                    case LOG: {
                        String msg = readString();
                        log(msg);
                        state = State.NONE;
                        break;
                    }
                    case MOVE: {
//                        double angle = readDouble();
//                        double dist = readDouble();
//                        log(angle + " " + dist);
                        MainActivity.bot.turn(readDouble());
                        MainActivity.bot.move(readDouble());
                        if (adapter.getFieldFragment() != null) {
                            adapter.getFieldFragment().redraw();
                        }
                        state = State.NONE;
                        break;
                    }
                    case SCAN: {
                        int degree = readVarInt();
                        double ir = readDouble();
                        log(degree + " " + ir);
                        adapter.getBotFragment().addScan(new Scan(degree, ir));
                        adapter.getBotFragment().redraw();
                        state = State.NONE;
                        break;
                    }
                    case OBJECT: {
                        int angle = readVarInt();
                        double distance = readDouble();
                        double width = readDouble();
                        log(angle + " " + distance + " " + width);
                        if (width > 4) {
                            field.addObject(new FieldObject(field.getBot(), angle, distance, width));
                        }
                        state = State.NONE;
                        break;
                    }
                    case LOGEDGE: {
                        String msg = readString();
                        int val = readVarInt();
                        log(msg + " " + val);
                        state = State.NONE;
                        break;
                    }
                }
            }
        } catch (InterruptedException ignored) {}
    }

    public String readString() throws InterruptedException {
        int len = readVarInt();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            builder.append((char)(bytes.take() & 0xff));
        }
        return builder.toString();
    }

    public int readVarInt() throws InterruptedException {
        int val = 0;
        int pos = 0;
        byte current;

        while (true) {
            current = bytes.take();
            val |= (current & 0x7f) << pos;

            if ((current & 0x80) == 0) break;
            pos += 7;
        }

        return val;
    }

//    public int readVarInta() throws InterruptedException {
//        int val = 0;
//        int pos = 0;
//        byte current;
//
//        while (true) {
//            current = bytes.take();
//            log("current: " + current);
//            val |= (current & 0x7f) << pos;
//
//            if ((current & 0x80) == 0) break;
//            pos += 7;
//        }
//
//        return val;
//    }

    public double readDouble() throws InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < 8; i++) {
            buffer.put(bytes.take());
        }
        buffer = (ByteBuffer) buffer.flip();

        return buffer.getDouble();
    }

//    public double readDoublea() throws InterruptedException {
//        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
//        for (int i = 0; i < 8; i++) {
//            byte a = bytes.take();
//            log("double: " + a);
//            buffer.put(a);
//        }
//        buffer = (ByteBuffer) buffer.flip();
//
//        return buffer.getDouble();
//    }

    public void log(String str) {
        activity.runOnUiThread(() -> {
            TextView logData = new TextView(activity);
            logData.setText(str);
            logData.setTextAppearance(R.style.logFont);

            LinearLayout logLayout = activity.findViewById(R.id.log);
            logLayout.addView(logData);
            ScrollView logScrollView = activity.findViewById(R.id.logScrollView);
            logScrollView.post(() -> logScrollView.fullScroll(View.FOCUS_DOWN));
        });
    }

    public void handleMessage() {
        String input = builder.toString();
        builder.setLength(0);

        int indexOfSplit = input.indexOf(":");
        if (indexOfSplit == -1) {
            Log.e("Wrong input", input);
            return;
        }
        String command = input.substring(0, indexOfSplit);
        String data = input.substring(indexOfSplit + 1);
        switch (command) {
            case "LOG": {
                activity.runOnUiThread(() -> {
                    TextView logData = new TextView(activity);
                    logData.setText(data);
                    logData.setTextAppearance(R.style.logFont);

                    LinearLayout logLayout = activity.findViewById(R.id.log);
                    logLayout.addView(logData);
                    ScrollView logScrollView = activity.findViewById(R.id.logScrollView);
                    logScrollView.post(() -> logScrollView.fullScroll(View.FOCUS_DOWN));
                });
                break;
            }
            case "MOA": {
                String[] moveData = data.split(",");
                MainActivity.bot.turn(Double.parseDouble(moveData[0]));
                MainActivity.bot.move(Double.parseDouble(moveData[1]) / 1000);
                if (adapter.getFieldFragment() != null) {
                    adapter.getFieldFragment().redraw();
                }
                break;
            }
            case "MOV": {
                String[] moveData = data.split(",");
                MainActivity.bot.turn(Double.parseDouble(moveData[0]));
                MainActivity.bot.move(Double.parseDouble(moveData[1]));
                if (adapter.getFieldFragment() != null) {
                    adapter.getFieldFragment().redraw();
                }
                break;
            }
            case "SCN": {
                String[] scanData = data.split(",");
                adapter.getBotFragment().addScan(new Scan(Double.parseDouble(scanData[0]), Double.parseDouble(scanData[1])));
                adapter.getBotFragment().redraw();
                break;
            }
            case "OBJ": {
                String[] objectData = data.split(",");
                Log.i("obj", input);
                if (Double.parseDouble(objectData[2]) > 4) {
                    field.addObject(new FieldObject(
                            field.getBot(),
                            Double.parseDouble(objectData[0]),
                            Double.parseDouble(objectData[1]),
                            Double.parseDouble(objectData[2])
                    ));
                }
                break;
            }
        }
    }
}
