package com.example.cybotclient;

import static com.example.cybotclient.Constants.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private SocketHandler client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

        client = new SocketHandler(this, CYBOT_IP_TEST, CYBOT_PORT);
        CheckBox incremental = findViewById(R.id.moveIncrement);

        findViewById(R.id.forward).setOnTouchListener(getOnTouchListener(incremental, B_MOVE_FORWARD_INC, B_MOVE_FORWARD));

        findViewById(R.id.left).setOnTouchListener(getOnTouchListener(incremental, B_MOVE_LEFT_INC, B_MOVE_LEFT));

        findViewById(R.id.reverse).setOnTouchListener(getOnTouchListener(incremental, B_MOVE_REVERSE_INC, B_MOVE_REVERSE));

        findViewById(R.id.right).setOnTouchListener(getOnTouchListener(incremental, B_MOVE_RIGHT_INC, B_MOVE_RIGHT));
    }

    @NonNull
    private View.OnTouchListener getOnTouchListener(CheckBox incremental, byte incrementalMove, byte nonIncrementalMove) {
        return (view, motionEvent) -> {
            view.performClick();
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (incremental.isChecked()) {
                    client.sendByte(incrementalMove);
                } else {
                    client.sendByte(nonIncrementalMove);
                }
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                if (!incremental.isChecked()) {
                    client.sendByte(B_MOVE_STOP);
                }
                return true;
            }
            return false;
        };
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
            RadioButton connectionStatus = findViewById(R.id.connection);
            connectionStatus.setButtonTintList(CONNECTING_COLOR);
            connectionStatus.setText(R.string.radio_disconnecting);

            client.disconnect();
        } else {
            RadioButton connectionStatus = findViewById(R.id.connection);
            connectionStatus.setButtonTintList(CONNECTING_COLOR);
            connectionStatus.setText(R.string.radio_connecting);

            new Thread(client).start();
        }
    }

    public void clearLog(View view) {
        LinearLayout layout = findViewById(R.id.log);

        layout.removeAllViews();
        ScrollView logScrollView = findViewById(R.id.logScrollView);
        logScrollView.post(() -> logScrollView.fullScroll(View.FOCUS_DOWN));
    }
}
