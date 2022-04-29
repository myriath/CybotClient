package com.example.cybotclient.activities;

import static com.example.cybotclient.util.Constants.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.cybotclient.R;
import com.example.cybotclient.communications.SocketHandler;
import com.example.cybotclient.data.field.Bot;
import com.example.cybotclient.data.field.Field;
import com.example.cybotclient.fragments.BotFragment;
import com.example.cybotclient.fragments.FieldFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final Field field = new Field();
    public static final Bot bot = field.getBot();

    private SocketHandler client;
    private PagerAdapter adapter;
    private int tabPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

        ViewPager2 pager = findViewById(R.id.pager);
        adapter = new PagerAdapter(this);
        pager.setAdapter(adapter);
        pager.setUserInputEnabled(false);

        TabLayout layout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(layout, pager, (tab, position) -> {
            if (position == 0) tab.setText("BOT");
            else tab.setText("FIELD");
        }).attach();
        layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        client = new SocketHandler(this, adapter, CYBOT_IP, CYBOT_PORT);
        field.setAdapter(adapter);
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
            if (client.isConnected()) {
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
            } else {
                toastConnectFirst();
            }
            return false;
        };
    }

    public void emergencyStop(View view) {
        if (client.isConnected()) {
            client.emergencyStop();
        }
    }

    public void scan(View view) {
        if (client.isConnected()) {
            client.sendByte(B_SCAN);
        }
    }

    public void clearFragment(int frag) {
        if (frag == 0) {
            adapter.getBotFragment().clear();
            adapter.getBotFragment().redraw();
        } else {
            adapter.getFieldFragment().clear();
            adapter.getFieldFragment().redraw();
        }
    }

    public void clearFragments(View view) {
        if (tabPosition == 0) {
            adapter.getBotFragment().clear();
            adapter.getBotFragment().redraw();
        } else {
            adapter.getFieldFragment().clear();
            adapter.getFieldFragment().redraw();
        }
    }

    public void sendCommand(View view) {
        EditText commandField = findViewById(R.id.commandField);
        String command = commandField.getText().toString();
        String[] params = command.split(" ");
        commandField.setText("");
        if (client.isConnected()) {
            new CommandThread(adapter, client, params).start();
        } else {
            toastConnectFirst();
        }
    }

    public void toastConnectFirst() {
        Toast.makeText(this, "Connect first!", Toast.LENGTH_SHORT).show();
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

    private static class CommandThread extends Thread {
        private final PagerAdapter adapter;
        private final SocketHandler client;
        private final String[] command;

        public CommandThread(PagerAdapter adapter, SocketHandler client, String[] command) {
            this.adapter = adapter;
            this.client = client;
            this.command = command;
        }

        @Override
        public void run() {
            try {
                switch (command[0]) {
                    // TODO: special cases go here
                    case "scanmove": {
                        CommandThread current = new CommandThread(adapter, client, new String[]{"scan180"});
                        current.start();
                        current.join();

                        client.sendCommand("turn " + MainActivity.field.getSmallest().getAngleFromRobot());
                        client.sendByte(B_WAIT);
                        client.sendCommand("move " + MainActivity.field.getSmallest().getDistanceFromRobot());
                        client.sendByte(B_WAIT);
                        break;
                    }
                    case "fullmove": {
                        CommandThread current;
                        for (int i = 0; i < 2; i++) {
                            adapter.getBotFragment().clear();
                            current = new CommandThread(adapter, client, new String[] {"scan360"});
                            current.start();
                            current.join();

                            client.sendCommand("turn " + MainActivity.field.getSmallest().getAngleFromRobot());
                            client.sendByte(B_WAIT);
                            client.sendCommand("move " + ((MainActivity.field.getSmallest().getDistanceFromRobot() - 5) * 10));
                            client.sendByte(B_WAIT);
                        }
                        break;
                    }
                    case "scan180": {
                        client.sendCommand("fullscan");
                        client.sendByte(B_WAIT);
                        while (client.outBusy()) {
                            Thread.sleep(100);
                        }
                        break;
                    }
                    case "scan360": {
                        for (int i = 0; i < 3; i++) {
                            client.sendCommand("fullscan");
                            client.sendByte(B_WAIT);
                            client.sendCommand("turn 120");
                            client.sendByte(B_WAIT);
                        }
                        while (client.outBusy()) {
                            Thread.sleep(100);
                        }
                        break;
                    }
                    default: {
                        client.sendByte((byte) (':' & 0xff));
                        for (int i = 0; i < command.length; i++) {
                            client.sendBytes(command[i].getBytes());
                            if (i != command.length - 1) client.sendByte((byte) (' ' & 0xff));
                        }
                        client.sendByte(B_NEWLINE);
                        break;
                    }
                }
            } catch (InterruptedException ignored) {}
        }
    }

    public static class PagerAdapter extends FragmentStateAdapter {
        private BotFragment page0;
        private FieldFragment page1;

        public PagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        public BotFragment getBotFragment() {
            return page0;
        }

        public FieldFragment getFieldFragment() {
            return page1;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    if (page0 == null) {
                        page0 = new BotFragment();
                    }
                    return page0;
                case 1:
                    if (page1 == null) {
                        page1 = new FieldFragment();
                    }
                    return page1;
                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
