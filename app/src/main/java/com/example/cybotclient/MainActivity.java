package com.example.cybotclient;

import static com.example.cybotclient.Constants.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private final DataHandler dataHandler = new DataHandler();

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

    private ConnectionHandler client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

        client = new ConnectionHandler(CYBOT_IP, CYBOT_PORT);

        findViewById(R.id.forward).setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                moveForward();
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                moveStop();
                return true;
            }
            return false;
        });

        findViewById(R.id.left).setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                moveLeft();
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                moveStop();
                return true;
            }
            return false;
        });

        findViewById(R.id.reverse).setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                moveReverse();
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                moveStop();
                return true;
            }
            return false;
        });

        findViewById(R.id.right).setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                moveRight();
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                moveStop();
                return true;
            }
            return false;
        });
    }

    public void moveForward() {
        client.sendByte(B_MOVE_FORWARD);
    }

    public void moveLeft() {
        client.sendByte(B_MOVE_LEFT);
    }

    public void moveReverse() {
        client.sendByte(B_MOVE_REVERSE);
    }

    public void moveRight() {
        client.sendByte(B_MOVE_RIGHT);
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
        RadioButton button = findViewById(R.id.connection);
        button.setButtonTintList(connectingColor);
        button.setText(R.string.radio_connecting);

        if (client.connect()) {
            button.setButtonTintList(connectedColor);
            button.setText(R.string.radio_connected);

            client.setListener(data -> {
                dataHandler.handle(data);
                if (dataHandler.ready()) {
                    LinearLayout layout = findViewById(R.id.log);
                    TextView logData = new TextView(this);
                    logData.setText(dataHandler.getPreppedMessage());
                    logData.setTextAppearance(R.style.logFont);
                    logData.setSingleLine(false);

                    layout.addView(logData);
                    ScrollView logScrollView = findViewById(R.id.logScrollView);
                    logScrollView.post(() -> {
                        logScrollView.fullScroll(View.FOCUS_DOWN);
                    });
                }
            });
        } else {
            button.setButtonTintList(disconnectedColor);
            button.setText(R.string.error_msg);
        }
    }
}