package com.example.cybotclient;

import static com.example.cybotclient.Constants.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private final DataHandler dataHandler = new DataHandler();

    private ConnectionHandler client;
    private Thread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

        client = new ConnectionHandler(this, CYBOT_IP_TEST, CYBOT_PORT);
        clientThread = new Thread(client);
        CheckBox incrementalCheck = findViewById(R.id.moveIncrement);

        findViewById(R.id.forward).setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (incrementalCheck.isChecked()) {
                    moveForwardInc();
                } else {
                    moveForward();
                }
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                if (!incrementalCheck.isChecked()) {
                    moveStop();
                }
                return true;
            }
            return false;
        });

        findViewById(R.id.left).setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (incrementalCheck.isChecked()) {
                    moveLeftInc();
                } else {
                    moveLeft();
                }
                return false;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                if (!incrementalCheck.isChecked()) {
                    moveStop();
                }
                return true;
            }
            return false;
        });

        findViewById(R.id.reverse).setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (incrementalCheck.isChecked()) {
                    moveReverseInc();
                } else {
                    moveReverse();
                }
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                if (!incrementalCheck.isChecked()) {
                    moveStop();
                }
                return true;
            }
            return false;
        });

        findViewById(R.id.right).setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (incrementalCheck.isChecked()) {
                    moveRightInc();
                } else {
                    moveRight();
                }
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                if (!incrementalCheck.isChecked()) {
                    moveStop();
                }
                return true;
            }
            return false;
        });
    }

    public void moveForward() {
        client.sendByte(B_MOVE_FORWARD);
    }
    public void moveForwardInc() {
        client.sendByte(B_MOVE_FORWARD_INC);
    }

    public void moveLeft() {
        client.sendByte(B_MOVE_LEFT);
    }
    public void moveLeftInc() {
        client.sendByte(B_MOVE_LEFT_INC);
    }

    public void moveReverse() {
        client.sendByte(B_MOVE_REVERSE);
    }
    public void moveReverseInc() {
        client.sendByte(B_MOVE_REVERSE_INC);
    }

    public void moveRight() {
        client.sendByte(B_MOVE_RIGHT);
    }
    public void moveRightInc() {
        client.sendByte(B_MOVE_RIGHT_INC);
    }

    public void moveStop() {
        client.sendByte(B_MOVE_STOP);
    }

    public void scan(View view) {
        client.sendByte(B_SCAN);
    }

    public void sendCommand(View view) {
        EditText commandField = findViewById(R.id.commandField);
        String command = commandField.getText().toString();

        client.sendByte((byte) ':');
        client.sendBytes(command.getBytes());
        client.sendByte(B_NEWLINE);

        commandField.setText("");
    }

    public void connect(View view) {
        if (client.isConnected()) {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            clientThread = new Thread(client);
            clientThread.start();
        }
    }

    public void clearLog(View view) {
        LinearLayout layout = findViewById(R.id.log);

        layout.removeAllViews();
        ScrollView logScrollView = findViewById(R.id.logScrollView);
        logScrollView.post(() -> logScrollView.fullScroll(View.FOCUS_DOWN));
    }
}