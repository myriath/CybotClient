package com.example.cybotclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final byte B_ESTABLISH_CONNECTION = (byte) 0x01;
    public static final byte B_SCAN = (byte) 0x02;
    public static final byte B_MOVE_STOP = (byte) 0x10;
    public static final byte B_MOVE_FORWARD = (byte) 0x11;
    public static final byte B_MOVE_LEFT = (byte) 0x12;
    public static final byte B_MOVE_REVERSE = (byte) 0x13;
    public static final byte B_MOVE_RIGHT = (byte) 0x14;

    public static final byte B_ACK_CONNECT = (byte) 0x02;
    public static final byte B_ACK_SCAN = (byte) 0x03;
    public static final byte B_ACK_STOP = (byte) 0x20;
    public static final byte B_ACK_FORWARD = (byte) 0x21;
    public static final byte B_ACK_LEFT = (byte) 0x22;
    public static final byte B_ACK_REVERSE = (byte) 0x23;
    public static final byte B_ACK_RIGHT = (byte) 0x24;

    private static final String CYBOT_IP = "192.168.1.1";
    private static final int CYBOT_PORT = 288;
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

    private final CyBotClient client = new CyBotClient(CYBOT_IP, CYBOT_PORT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

        findViewById(R.id.forward).setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            try {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    moveForward();
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    moveStop();
                    return true;
                }
            } catch (IOException e) {
                return false;
            }
            return false;
        });

        findViewById(R.id.left).setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            try {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    moveLeft();
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    moveStop();
                    return true;
                }
            } catch (IOException e) {
                return false;
            }
            return false;
        });

        findViewById(R.id.reverse).setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            try {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    moveReverse();
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    moveStop();
                    return true;
                }
            } catch (IOException e) {
                return false;
            }
            return false;
        });

        findViewById(R.id.right).setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            try {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    moveRight();
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    moveStop();
                    return true;
                }
            } catch (IOException e) {
                return false;
            }
            return false;
        });
    }

    public void moveForward() throws IOException {
        if (client.sendByte(B_MOVE_FORWARD) == B_ACK_FORWARD) {

        }
    }

    public void moveLeft() throws IOException {
        if (client.sendByte(B_MOVE_LEFT) == B_ACK_LEFT) {

        }
    }

    public void moveReverse() throws IOException {
        if (client.sendByte(B_MOVE_REVERSE) == B_ACK_REVERSE) {

        }
    }

    public void moveRight() throws IOException {
        if (client.sendByte(B_MOVE_RIGHT) == B_ACK_RIGHT) {

        }
    }

    public void moveStop() throws IOException {
        if (client.sendByte(B_MOVE_STOP) == B_ACK_STOP) {

        }
    }

    public void scan(View view) throws IOException {
        if (client.sendByte(B_SCAN) == B_ACK_SCAN) {

        }
    }

    public void connect(View view) throws IOException{
        boolean connected = false;
        RadioButton button = findViewById(R.id.connection);
        button.setButtonTintList(connectingColor);
        button.setText(R.string.radio_connecting);

        client.connect();
        if (client.sendByte(B_ESTABLISH_CONNECTION) ==  B_ACK_CONNECT) {
            connected = true;
        }

        if (connected) {
            button.setButtonTintList(connectedColor);
            button.setText(R.string.radio_connected);
        } else {
            button.setButtonTintList(disconnectedColor);
            button.setText(R.string.error_msg);
        }
    }
}