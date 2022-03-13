package com.example.cybotclient;

import static com.example.cybotclient.Constants.*;

import java.util.LinkedList;
import java.util.Queue;

public class DataHandler {
    private enum State {
        WAITING, READING_MESSAGE
    }
    private State currentState;
    private boolean messageReady;

    private Queue<Byte> currentMsgQueue;
    private String preppedMessage;

    public DataHandler() {
        currentState = State.WAITING;
        messageReady = false;
        currentMsgQueue = new LinkedList<>();
    }

    public void handle(byte data) {
        switch (data) {
            case B_START_MESSAGE: {
                if (currentState == State.WAITING) {
                    currentState = State.READING_MESSAGE;
                } else {
                    currentMsgQueue.add(data);
                }
                break;
            }
            case B_NEWLINE: {
                if (currentState == State.READING_MESSAGE) {
                    buildMessage();
                }
                break;
            }
            case B_END_MESSAGE: {
                if (currentState == State.READING_MESSAGE) {
                    currentState = State.WAITING;
                    buildMessage();
                }
                break;
            }
            default: {
                if (currentState == State.READING_MESSAGE) {
                    currentMsgQueue.add(data);
                }
            }
        }
    }

    public void buildMessage() {
        StringBuilder builder = new StringBuilder();
        while (!currentMsgQueue.isEmpty()) {
            builder.append((char) currentMsgQueue.remove().byteValue());
        }
        preppedMessage = builder.toString();
        messageReady = true;
    }

    public boolean ready() {
        boolean ret = messageReady;
        messageReady = false;
        return ret;
    }

    public String getPreppedMessage() {
        return preppedMessage;
    }
}
