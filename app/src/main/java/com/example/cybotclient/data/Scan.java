package com.example.cybotclient.data;

import com.example.cybotclient.activities.MainActivity;

public class Scan {
    public double angle;
    public double distance;

    public Scan(double angle, double distance) {
        double robotRadians = Math.toRadians(MainActivity.bot.getAngle());
        //TODO: decide if this works without -90
        double objRadians = Math.toRadians(MainActivity.bot.getAngle() /*- 90*/ + angle);
        double scannerX = 18.75 * Math.cos(robotRadians);
        double scannerY = 18.75 * Math.sin(robotRadians);
        double objX = distance * Math.cos(objRadians) + scannerX;
        double objY = distance * Math.sin(objRadians) + scannerY;

        this.angle = (Math.toDegrees(Math.atan2(objY, objX)) + 360) % 360;
        this.distance = Math.sqrt(objX * objX + objY * objY) - 18.75;
    }
}
