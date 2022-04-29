package com.example.cybotclient.data.field;

import java.util.LinkedList;

public class Bot extends FieldObject{
    public static final double BOT_RADIUS = 18.75;
    private double angle;

    private LinkedList<Position> positions;

    public Bot() {
        super(0, 0, BOT_RADIUS);
        this.angle = 0;
        positions = new LinkedList<>();
        positions.add(new Position(0, 0));
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }

    public void turn(double degrees) {
        setAngle(angle + degrees);
    }

    public LinkedList<Position> getPositions() {
        return positions;
    }

    public void move(double dist) {
        double rad = Math.toRadians(angle);
        setX(getX() + dist * Math.cos(rad));
        setY(getY() + dist * Math.sin(rad));
        positions.add(new Position(getX(), getY()));
    }
}
