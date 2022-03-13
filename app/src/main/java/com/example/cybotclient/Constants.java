package com.example.cybotclient;

import android.content.res.ColorStateList;
import android.graphics.Color;

public class Constants {
    public static final ColorStateList DISCONNECTED_COLOR = new ColorStateList(
            new int[][] {
                    new int[] {-android.R.attr.state_enabled},
                    new int[] {android.R.attr.state_enabled}
            },
            new int[]{
                    Color.BLACK,
                    Color.rgb(147, 29, 29)
            }
    );
    public static final ColorStateList CONNECTING_COLOR = new ColorStateList(
            new int[][] {
                    new int[] {-android.R.attr.state_enabled},
                    new int[] {android.R.attr.state_enabled}
            },
            new int[]{
                    Color.BLACK,
                    Color.rgb(237, 190, 20)
            }
    );
    public static final ColorStateList CONNECTED_COLOR = new ColorStateList(
            new int[][] {
                    new int[] {-android.R.attr.state_enabled},
                    new int[] {android.R.attr.state_enabled}
            },
            new int[]{
                    Color.BLACK,
                    Color.rgb(19, 166, 40)
            }
    );

    public static final byte B_SCAN = (byte) 0x01;
    public static final byte B_MOVE_STOP = (byte) 0x10;
    public static final byte B_MOVE_FORWARD = (byte) 0x11;
    public static final byte B_MOVE_FORWARD_INC = (byte) 0x15;
    public static final byte B_MOVE_LEFT = (byte) 0x12;
    public static final byte B_MOVE_LEFT_INC = (byte) 0x16;
    public static final byte B_MOVE_REVERSE = (byte) 0x13;
    public static final byte B_MOVE_REVERSE_INC = (byte) 0x17;
    public static final byte B_MOVE_RIGHT = (byte) 0x14;
    public static final byte B_MOVE_RIGHT_INC = (byte) 0x18;
    public static final byte B_START_MESSAGE = (byte) 0x7f;
    public static final byte B_END_MESSAGE = (byte) 0x7e;
    public static final byte B_NEWLINE = (byte) '\n';

    public static final byte B_END_COMMS = (byte) 0x0f;

    public static final String CYBOT_IP = "192.168.1.1";
    public static final String CYBOT_IP_TEST = "192.168.0.157";
    public static final int CYBOT_PORT = 288;
}
