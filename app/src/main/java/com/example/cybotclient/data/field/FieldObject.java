package com.example.cybotclient.data.field;

import android.util.Log;

import com.example.cybotclient.activities.MainActivity;

public class FieldObject {
    private double x;
    private double y;
    private double radius;
    private boolean smallest;

    public FieldObject(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        smallest = false;
    }

    public FieldObject(Bot robot, double angle, double distance, double width) {
        double robotRadians = Math.toRadians(robot.getAngle());
        double objRadians = Math.toRadians(robot.getAngle() - 90 + angle);
        double scannerX = 18.75 * Math.cos(robotRadians);
        double scannerY = 18.75 * Math.sin(robotRadians);

        Log.i("obj0", "" + (distance + width / 2.0));
        Log.i("obj1", objRadians + "");
        Log.i("obj2", (distance + width / 2.0) * Math.sin(objRadians) + "");

        x = (distance + width / 2.0) * Math.cos(objRadians) + scannerX + robot.getX();
        y = (distance + width / 2.0) * Math.sin(objRadians) + scannerY + robot.getY();
        radius = width / 2.0d;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setRadius(double r) {
        radius = r;
    }

    public double getDistanceFromRobot() {
        Bot bot = MainActivity.field.getBot();
        double x = getX() - bot.getX();
        double y = getY() - bot.getY();
        return Math.sqrt(x * x + y * y) - 18.75 - radius;
    }

    public double getAngleFromRobot() {
        Bot bot = MainActivity.field.getBot();
        double x = getX() - bot.getX();
        double y = getY() - bot.getY();
        Log.i("angle0", x + " " + y);
        Log.i("angle1", String.valueOf((Math.toDegrees(Math.atan2(y, x)) + 360) % 360));
        Log.i("angle2", String.valueOf(bot.getAngle()));
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360 - (bot.getAngle() % 360);
    }

    @Override
    public String toString() {
        return "FieldObject{" +
                "x=" + x +
                ", y=" + y +
                ", radius=" + radius +
                '}';
    }
}
