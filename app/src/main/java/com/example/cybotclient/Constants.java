package com.example.cybotclient;

public class Constants {
    public static final byte B_SCAN = (byte) 0x01;
    public static final byte B_MOVE_STOP = (byte) 0x10;
    public static final byte B_MOVE_FORWARD = (byte) 0x11;
    public static final byte B_MOVE_LEFT = (byte) 0x12;
    public static final byte B_MOVE_REVERSE = (byte) 0x13;
    public static final byte B_MOVE_RIGHT = (byte) 0x14;
    public static final byte B_START_MESSAGE = (byte) 0xff;
    public static final byte B_END_MESSAGE = (byte) 0xfe;
    public static final byte B_NEWLINE = (byte) '\n';

    public static final String CYBOT_IP = "192.168.1.1";
    public static final int CYBOT_PORT = 288;
}
